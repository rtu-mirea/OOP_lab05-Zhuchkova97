package game;

import java.io.Serializable;

// Корабль
public class Ship implements Serializable
{
    // Координата x начала корабля
    private int x;
    // Координата y начала корабля
    private int y;
    // Длина корабля
    private int length;
    // Расположение корабля: true - горизонтально, false - вертикально
    private boolean isHorizontal;
    // Количество попаданий
    private int wounds;

    // Конструктор класса
    Ship(int x, int y, int length, boolean isHorizontal) throws Exception
    {
        // Проверяем ввод координат. Они должны быть не отрицательны, иначе выбрасываем исключение
        if ( x < 0 | y < 0)
        {
            throw new Exception("Координаты корабля не могут быть отрицательными числами!");
        }
        // Проверяем ввод длины. Она должна быть > 1, иначе выбрасываем исключение
        if (length < 1)
        {
            throw new Exception("Длина корабля должна быть представлена положительным числом!");
        }
        // Присваиваем переменным класса значения входных переменных
        this.x = x;
        this.y = y;
        this.length = length;
        this.isHorizontal = isHorizontal;
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

    // Возвращает значение переменной length
    public int getLength()
    {
        return this.length;
    }

    // Возвращает значение переменной isHorizontal
    public boolean getIsHorizontal()
    {
        return isHorizontal;
    }

    // Ранить корабль. Возвращает true, если корабль уничтожен
    public boolean makeWound() throws Exception
    {
        // Если корабль уничтожен, его нельзя атаковать. Иначе выбрасывается исключение
        if (isDestroyed())
        {
            throw new Exception("Нельзя атаковать уничтоженный корабль!");
        }
        // Увеличиваем количество попаданий
        wounds++;
        // Если после попадания корабль уничтожен, то возвращаем true
        if (isDestroyed()) return true; else return false;
    }

    // Корабль уничтожен?
    public boolean isDestroyed()
    {
        if (wounds < length) return false; else return true;
    }
}
