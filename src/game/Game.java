package game;

import java.io.Serializable;

// Игра
public class Game implements Serializable
{
    // Игровые поля
    private BattleField[] fields = new BattleField[2];
    // Игроки
    protected Gamer[] players = new Gamer[2];
    // Номер игрока, которому принадлежит следующий ход: 0 - первый игрок, 1 - второй игрок
    private int rightPlayer;
    // 0, если игра ещё не окончена; либо номер выигравшего игрока
    private int gameIsOver;

    // Конструктор класса
    Game(Gamer player1, Gamer player2)
    {
        // Присваиваем входные переменные внутренним переменным класса
        players[0] = player1;
        players[1] = player2;

        // Инициализуем и присваиваем переменные
        fields[0] = new BattleField();
        fields[1] = new BattleField();

        rightPlayer = (int) (Math.random());
    }

    public void setRightPlayer(int rightPlayer)
    {
        this.rightPlayer = rightPlayer;
    }

    // Возвращаем номер игрока, которому принадлежит следующий ход
    public int getRightPlayer()
    {
        return rightPlayer;
    }

    // Сделать ход. Возвращается 0 в случае промаха, 1 - в случае попадания, 2 - в случае уничтожения, 3 - в случае, если все корабли уничтожены
    public int makeMove(Shot shot, int playerNumber) throws Exception
    {
        // Если игра окончена, то выбрасываем исключение
        if (gameIsOver != 0)
        {
            throw new Exception("Игра окончена! Ходить больше нельзя!");
        }

        // Проверка: тот ли игрок сейчас ходит
        if (playerNumber != rightPlayer)
        {
            throw new Exception("Сейчас должен ходить другой игрок!");
        }
        int fieldsIndex;
        // Определяем номер поля противника
        if (rightPlayer == 0) fieldsIndex = 1; else fieldsIndex = 0;
        // Производим выстрел. Записываем результат выстрела в переменную
        int shotResult = fields[fieldsIndex].makeShot(shot);
        // Обработка результатов выстрела. Если 0, значит игрок промахнулся. Передаём ход другому игроку
        if (shotResult == 0)
        {
            if (rightPlayer == 0) rightPlayer = 1; else rightPlayer = 0;
            return shotResult;
        }
        // Если 1, значит игрок ранил. Игрок ходит повторно
        else if (shotResult == 1) return shotResult;
        // Если 2, значит игрок уничтожил корабль. Игрок ходит повторно
        else if (shotResult == 2) return shotResult;
        // Если 3, значит игрок уничтожил последний корабль. Игрок победил. Игра окончена
        else if (shotResult == 3)
        {
            // Записываем номер победившего игрока
            gameIsOver = playerNumber + 1;
            return shotResult;
        }
        else
        {
            throw new Exception("Неопределённый результат!");
        }
    }

    public BattleField getBattleField(int i)
    {
        return fields[i];
    }

    public int getGameIsOver()
    {
        return gameIsOver;
    }
}
