package game;

import java.io.*;


// Класс Морской Бой. Основной класс программы
public class BattleShip implements Serializable
{
    // Текущая активная игра
    protected Game currentGame;

    // Добавить нового игрока в систему
    public void addUser(String name, String login, String password) throws Exception
    {
        // Если логин занят, то бросаем исключение
        if (isLoginBusy(login))
        {
            throw new Exception("Данный логин занят другим пользователем!");
        }
        // Запись данных пользователя в файл
        writeUserToFile(name, login, password);
    }

    // Записать данные пользователя в файл
    private void writeUserToFile(String name, String login, String password) throws Exception
    {
        // Открываем файл с пользователями для дозаписи
        FileWriter file = new FileWriter("data/users/users.txt", true);
        // Дописываем данные о пользователе в строку
        file.write(name);
        file.append(" ");
        file.write(login);
        file.append(" ");
        file.write(password);
        file.append("\n");
        // Закрываем файл
        file.close();
    }

    // Данный логин занят другим пользователем?
    public boolean isLoginBusy(String login) throws Exception
    {
        // Во время операций с файлом могут произойти непредвиденные ситуации, поэтому весь код работы с файлом поместим в блок try
        try
        {
            // Открываем файл с данными пользователей в режиме для чтения
            FileReader file = new FileReader("data/users/users.txt");
            // Создаём объект для построчного считывания файла. Передаём ему в конструкторе открытый ранее файл
            LineNumberReader reader = new LineNumberReader(file);
            // В цикле считываем строки
            while(true)
            {
                // Считали очередную строку
                String line = reader.readLine();
                // Если line == null, значит все строки считаны. Выходим из цикла
                if (line == null) break;
                // Делим строку на части по разделителю пробел
                String[] user = line.split(" ");
                // Параметр с индексом 1 - логин пользователя. Если логин из файла совпадает с переменной login, значит данный логин занят. Возвращаем true. Завершаем выполнение функции
                if (login.equals(user[1]))
                {
                    // Закрывает поток и освобождает любые системные ресурсы, связанные с ним
                    reader.close();
                    // Закрывает поток и освобождает любые системные ресурсы, связанные с ним
                    file.close();
                    return true;
                }
            }
            // Закрывает поток и освобождает любые системные ресурсы, связанные с ним
            reader.close();
            // Закрывает поток и освобождает любые системные ресурсы, связанные с ним
            file.close();
            // Введёный логин не занят, иначе выполнение функции завершилось бы ещё в цикле. Возвращаем false
            return false;
        }
        // В случае, если файл с информацией о пользователях не был найден, создадим новый
        catch (java.io.FileNotFoundException ex)
        {
            File file = new File("data/users/users.txt");
            file.createNewFile();
            // Введёный логин не занят, так как файл пуст. Возвращаем false
            return false;
        }
    }

    // Считываем данные игрока, имеющего данный логин
    public Gamer getGamerByLogin(String login) throws Exception
    {
        // Открываем файл с данными пользователей в режиме для чтения
        FileReader file = new FileReader("data/users/users.txt");
        // Создаём объект для построчного считывания файла. Передаём ему в конструкторе открытый ранее файл
        LineNumberReader reader = new LineNumberReader(file);
        // В цикле считываем строки
        while(true)
        {
            // Считали очередную строку
            String line = reader.readLine();
            // Если line == null, значит все строки считаны. Выходим из цикла
            if (line == null) break;
            // Делим строку на части по разделителю пробел
            String[] user = line.split(" ");
            // Параметр с индексом 1 - логин пользователя. Если логины свопадают, то возвращаем данного пользователя
            if (login.equals(user[1]))
            {
                // Закрывает поток и освобождает любые системные ресурсы, связанные с ним
                reader.close();
                // Закрывает поток и освобождает любые системные ресурсы, связанные с ним
                file.close();
                // Возвращаем данного пользователя
                return new Gamer(user[0], user[1], user[2]);
            }
        }
        // Закрывает поток и освобождает любые системные ресурсы, связанные с ним
        reader.close();
        // Закрывает поток и освобождает любые системные ресурсы, связанные с ним
        file.close();
        // Выбрасываем исключение
        throw new Exception("Пользователь с данным логином не найден!");
    }

    // Начать новую игру
    public void startNewGame(Gamer player1, Gamer player2) throws Exception
    {
        // Если предыдущая игра ещё не завершена, вызываем исключение
        if (currentGame != null)
        {
            throw new Exception("Сначала нужно завершить предыдущую игру!");
        }

        // Проверим, верно ли введены логин и пароль пользователя
        // Проверим корректность ввода логинов
        if (!isLoginBusy(player1.getLogin()))
        {
            throw new Exception("У первого игрока не зарегистрированный логин!");
        }
        if (!isLoginBusy(player2.getLogin()))
        {
            throw new Exception("У второго игрока не зарегистрированный логин!");
        }
        // Получаем из файла данные пользователей с ведёнными логинами
        Gamer canonPlayer1 = getGamerByLogin(player1.getLogin());
        Gamer canonPlayer2 = getGamerByLogin(player2.getLogin());
        // Проверим корректность ввода паролей
        if (!canonPlayer1.getPassword().equals(player1.getPassword()))
        {
            throw new Exception("У первого игрока не верный пароль!");
        }
        if (!canonPlayer2.getPassword().equals(player2.getPassword()))
        {
            throw new Exception("У второго игрока не верный пароль!");
        }

        // Создаём новую игру
        currentGame = new Game(canonPlayer1, canonPlayer2);
    }
}