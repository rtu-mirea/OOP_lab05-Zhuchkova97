package game;

import java.io.Serializable;

// Класс Игрок
public class Gamer extends User implements Serializable
{
    // Переменная используется при сериализации
    private static final long serialVersionUID = 1;

    Gamer(String name, String login, String password)
    {
        super(name, login, password);
    }
}
