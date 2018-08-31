package ru.nastinio;

import java.io.*;

public class FileWorker {

    public void write(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст у файл
                out.print(text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read(String fileName) throws FileNotFoundException {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        File file = new File(fileName);
        if (!file.exists()){
            throw new FileNotFoundException(file.getName());
        }

        try {
            //Объект для чтения файла в буфер
            FileInputStream fStream = new FileInputStream(file);
            InputStreamReader inReader = new InputStreamReader(fStream, "windows-1251");
            BufferedReader in = new BufferedReader(inReader);
            // BufferedReader in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        //Возвращаем полученный текст с файла
        return sb.toString();
    }

    public void update(String fileName, String newText) throws FileNotFoundException {
        try{
            exists(fileName);
            StringBuilder sb = new StringBuilder();
            String oldFile = read(fileName);
            sb.append(oldFile);
            sb.append(newText);
            write(fileName, sb.toString());
        }catch (FileNotFoundException e){
            throw e;
        }
    }

    public void delete(String nameFile) throws FileNotFoundException {
        exists(nameFile);
        new File(nameFile).delete();
    }

    private void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()){
            throw new FileNotFoundException(file.getName());
        }
    }
}
