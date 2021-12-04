import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    static StringBuilder sb = new StringBuilder();

    public static void createDir(String name) {
        File dir = new File(name);
        if (dir.mkdir())
            sb.append(name).append(" has been created").append("\n");
    }

    public static void createFile(String name) {
        File file = new File(name);
        try {
            if (file.createNewFile())
                sb.append(name).append(" has been created").append("\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void writeLog(String path, boolean append, StringBuilder log) {
        try (FileWriter wr = new FileWriter(path, append)) {
            wr.write(log.toString());
            wr.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveGame(String path, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
            sb.append(path).append(" has been created").append("\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static GameProgress openProgress(String path) {
        GameProgress gp = null;
        try (FileInputStream fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gp = (GameProgress) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return gp;
    }

    public static void zipFiles(String path, String[] pathList) {
        try {
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(path));
            for (String x : pathList) {
                FileInputStream fis = new FileInputStream(x);
                ZipEntry entry = new ZipEntry(x);
                zout.putNextEntry(entry);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                zout.write(buffer);
                zout.closeEntry();
                sb.append(x).append(" has been packed at: ").append(path).append("\n");
            }
            zout.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void openZip(String zipPath, String dirPath) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
                sb.append(name).append(" has been unpacked at: ").append(dirPath).append("\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deletFile(String[] pathList) {
        for (String x : pathList) {
            File file = new File(x);
            if (file.delete()) {
                sb.append(x).append(" has been deleted").append("\n");
            }
        }
    }

    public static void main(String[] args) {
        String[] dirList = {
                "/home/irmate/Документы/Games/src",
                "/home/irmate/Документы/Games/res",
                "/home/irmate/Документы/Games/savegames",
                "/home/irmate/Документы/Games/temp",
                "/home/irmate/Документы/Games/src/main",
                "/home/irmate/Документы/Games/src/test",
                "/home/irmate/Документы/Games/res/drawables",
                "/home/irmate/Документы/Games/res/vectors",
                "/home/irmate/Документы/Games/res/icons"
        };
        String[] fileList = {
                "/home/irmate/Документы/Games/src/main/Main.java",
                "/home/irmate/Документы/Games/src/main/Utils.java",
                "/home/irmate/Документы/Games/temp/temp.txt"
        };
        for (String dir : dirList) {
            createDir(dir);
        }
        for (String file : fileList) {
            createFile(file);
        }
        String[] pathListForSave = {
                "/home/irmate/Документы/Games/savegames/save1.dat",
                "/home/irmate/Документы/Games/savegames/save2.dat",
                "/home/irmate/Документы/Games/savegames/save3.dat"
        };
        GameProgress[] gameProgressSaveList = {
                new GameProgress(78, 2, 57, 100),
                new GameProgress(43, 9, 32, 87),
                new GameProgress(99, 4, 89, 180)
        };
        for (int i = 0; i < pathListForSave.length; i++) {
            saveGame(pathListForSave[i], gameProgressSaveList[i]);
        }
        zipFiles("/home/irmate/Документы/Games/savegames/saves.zip", pathListForSave);
        deletFile(pathListForSave);
        openZip("/home/irmate/Документы/Games/savegames/saves.zip", "/home/irmate/Документы/Games/savegames");
        writeLog("/home/irmate/Документы/Games/temp/temp.txt", false, sb);
        System.out.println(openProgress("/home/irmate/Документы/Games/savegames/save1.dat"));
    }
}