package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Поле битвы
public class BattleField implements Serializable
{
    // Количество однопалубных кораблей
    private int oneDeckShipCount;
    // Количество двухпалубных кораблей
    private int twoDeckShipCount;
    // Количество трёхпалубных кораблей
    private int threeDeckShipCount;
    // Количество четырёхпалубных кораблей
    private int fourDeckShipCount;
    // Количество уничтоженных кораблей
    private int shipsDestroyedCount;
    // 0, если игра окончена; либо номер выигравшего игрока
    private int gameIsOver;
    // Игровое поле. Каждая ячейка массива - игровая клетка
    private Ship[][] field = new Ship[10][10];
    // Список выстрелов
    ArrayList<Shot> shots = new ArrayList<Shot>();

    // Добавить корабль на поле
    public void addShip(Ship ship) throws Exception
    {
        // Проверка: превышено ли количество кораблей данной длины
        if (isShipLimitExceeded(ship))
        {
            throw new Exception("Превышен лимит кораблей данного типа!");
        }

        // Проверка: умещается ли корабль в пределах поля
        if (!isShipFitsOnField(ship))
        {
            throw new Exception("Расположение корабля выходит за пределы игрового поля!");
        }

        // Проверка: свободна ли площадка под кораблём (не стоят ли там другие корабли)
        if (!isPlatformFree(ship))
        {
            throw new Exception("Расположению корабля мешают другие корабли!");
        }

        // Непосредственно помещает корабль на поле
        putShipOnField(ship);
    }

    // Превышено ли количество кораблей данной длины(true - превышено)
    public boolean isShipLimitExceeded(Ship ship) throws Exception
    {
        int length = ship.getLength();
        if (length == 4)
        {
            if (fourDeckShipCount < 1) return false;
            else return true;
        }
        if (length == 3)
        {
            if (threeDeckShipCount < 2) return false;
            else return true;
        }
        if (length == 2)
        {
            if (twoDeckShipCount < 3) return false;
            else return true;
        }
        if (length == 1)
        {
            if (oneDeckShipCount < 4) return false;
            else return true;
        }
        else
        {
            throw new Exception("Длина корабля не соответствует допустимым значениям!");
        }
    }

    // Проверка: умещается ли корабль в пределах поля
    public static boolean isShipFitsOnField(Ship ship)
    {
        // Проверка происходит по разному для кораблей расположеных горизонтально и вертикально
        if (ship.getIsHorizontal())
        {
            // Если истинно, то корабль находится в пределах поля
            if (ship.getX() + ship.getLength() <= 10) return true;
            else return false;
        }
        else
        {
            // Если истинно, то корабль находится в пределах поля
            if (ship.getY() + ship.getLength() <= 10) return true;
            else return false;
        }
    }

    // Проверка: свободна ли площадка под кораблём (не стоят ли там другие корабли)
    public boolean isPlatformFree(Ship ship)
    {
        // Проверка происходит по разному для кораблей расположеных горизонтально и вертикально
        if (ship.getIsHorizontal())
        {
            for (int i = ship.getX() - 1; i <= ship.getX() + ship.getLength(); i++)
            {
                for (int j = ship.getY() - 1; j <= ship.getY() + 1; j++)
                {
                    if (i < 0 | j < 0 | i > 9 | j > 9) continue;
                    if (field[i][j] != null) return false;
                }
            }
            return true;
        }
        else
        {
            for (int i = ship.getX() - 1; i <= ship.getX() + 1; i++)
            {
                for (int j = ship.getY() - 1; j <= ship.getY() + ship.getLength(); j++)
                {
                    if (i < 0 | j < 0 | i > 9 | j > 9) continue;
                    if (field[i][j] != null) return false;
                }
            }
            return true;
        }
    }

    // Непосредственно помещает корабль на поле
    public void putShipOnField(Ship ship)
    {
        int x = ship.getX();
        int y = ship.getY();

        // Установка происходит по разному для кораблей расположеных горизонтально и вертикально
        if (ship.getIsHorizontal())
        {
            for (int i = x; i < x + ship.getLength(); i++)
            {
                field[i][y] = ship;
            }
        }
        else
        {
            for (int i = y; i < y + ship.getLength(); i++)
            {
                field[x][i] = ship;
            }
        }

        // Увеличиваем количество кораблей данного типа
        int length = ship.getLength();
        if (length == 4) fourDeckShipCount++;
        if (length == 3) threeDeckShipCount++;
        if (length == 2) twoDeckShipCount++;
        if (length == 1) oneDeckShipCount++;
    }

    // Расстановка кораблей окончена?
    public boolean isShipPlacementOver()
    {
        if (oneDeckShipCount == 4 & twoDeckShipCount == 3 & threeDeckShipCount == 2 & fourDeckShipCount == 1)
            return true; else return false;
    }

    // Все корабли уничтожены?
    public boolean isAllShipsDestroyed()
    {
        if (shipsDestroyedCount < 10) return false; else return true;
    }

    // В эту координату стреляли ранее?
    public boolean isShotAtThisPoint(Shot shot)
    {
        // Перебираем элементы списка. Если будет совпадение, то в эту координату уже стреляли
        for(Shot i : shots)
        {
            if (i.getX() == shot.getX() & i.getY() == shot.getY()) return true;
        }
        // Если совпадений не было, значит в эту точку ещё не стреляли
        return false;
    }

    // Нанести удар по кораблям. Возвращается 0 в случае промаха, 1 - в случае попадания, 2 - в случае уничтожения, 3 - в случае, если все корабли уничтожены
    public int makeShot(Shot shot) throws Exception
    {
        int x = shot.getX();
        int y = shot.getY();

        // Проверка: не окончилась ли игра. Если да, то удар нанести уже не возможно
        if (gameIsOver != 0)
        {
            throw new Exception("Игра окончена!");
        }

        // Проверка: окончена ли расстановка кораблей. Иначе исключение
        if (!isShipPlacementOver())
        {
            throw new Exception("Расстановка кораблей ещё не окончена!");
        }

        // Проверка: стреляли ли ранее в эту координату. Если стреляли, то исключение
        if (isShotAtThisPoint(shot))
        {
            throw new Exception("В эту координату уже стреляли ранее!");
        }

        // Добавляем данный выстрел в список
        shots.add(shot);

        // Если ячейка равна null, значит эта ячейка не занята кораблями
        if (field[x][y] == null)
        {
            return 0;
        }
        // Иначе производим выстрел по кораблю
        else
        {
            // Если makeWound() возвращает true, значит корабль уничтожен
            if(field[x][y].makeWound())
            {
                // Увеличиваем счётчик уничтоженных кораблей
                shipsDestroyedCount++;
                // Если все корабли уничтожены - игра окончена, возвращаем 3, иначе возвращаем 2
                if (isAllShipsDestroyed()) return 3; else return 2;
            }
            // Если makeWound() возвращает false, значит корабль ранен, возвращаем 1
            else return 1;
        }
    }

    public ArrayList<Shot> getShots()
    {
        return shots;
    }

    public Ship[][] getField()
    {
        return field;
    }

    // Возвращает true, если корабль расположен в данной ячейке
    public boolean isShipLocatedInCell(int x, int y)
    {
        if (field[x][y] != null) return true;
        else return false;
    }
}
