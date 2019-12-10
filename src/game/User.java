package game;

import java.io.Serializable;

// Класс Пользователь
public class User implements Serializable
{
    // Имя пользователя
    private String name;
    // Логин пользователя
    private String login;
    // Пароль пользователя
    private String password;

    // Конструктор класса
    User(String name, String login, String password)
    {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    // Возвращает имя пользователя
    public String getName()
    {
        return name;
    }

    // Возвращает логин пользователя
    public String getLogin()
    {
        return login;
    }

    // Возвращает пароль пользователя
    public String getPassword()
    {
        return password;
    }
}
