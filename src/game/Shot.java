package game;

import java.io.Serializable;

// Снаряд
public class Shot implements Serializable
{
    // Координата попадания снаряда х
    private int x;
    // Координата попадания снаряда y
    private int y;

    // Конструктор класса с параметрами
    Shot(int x, int y) throws Exception
    {
        // Проверка: не выходят ли координаты атаки за границы игрового поля
        if (x < 0 | x > 9 | y < 0 | y > 9)
        {
            throw new Exception("Координаты атаки выходят за границы игрового поля!");
        }

        // Сохраняем значения входных переменных в переменных класса
        this.x = x;
        this.y = y;
    }

    // Возвращает значение переменной x
    public int getX()
    {
        return this.x;
    }

    // Возвращает значение переменной y
    public int getY()
    {
        return this.y;
    }
}
