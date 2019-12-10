package game;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import javafx.stage.Modality;


// Графическая оболочка(приложение) игры
public class Application extends javafx.application.Application
{
    // Основное окно приложения
    private Stage stage;
    // Класс, предоставляющий методы для ведения игры
    private BattleShip battleShip = new BattleShip();
    // Начальное окно при запуске приложения
    private StartMenuWindow startMenuWindow = new StartMenuWindow();
    // Окно для авторизации игроков перед началом новой игры
    private LoginWindow loginWindow = new LoginWindow();
    // Окно для расстановки кораблей
    private ShipArrangementWindow shipArrangementWindow;
    // Окно для ведения боя
    private BattleWindow battleWindow;
    // Окно для загрузки игры
    private LoadGameWindow loadGameWindow = new LoadGameWindow();
    // Окно для добавления игроков
    private AddPlayerWindow addPlayerWindow = new AddPlayerWindow();
    // Панель компоновки. Позволяет размещать в себе другие элементы. В конструкторе принимает дочерние элементы
    private StackPane root;


    // Запуск приложения
    public void run(String[] args)
    {
        javafx.application.Application.launch(args);
    }

    // Метод автоматически выполняется после метода run()
    @Override
    public void start(Stage stage)
    {
        this.stage = stage;
        // Назначает функции для обработки событий
        assignFunctionsToHandleEvents();
        // Stage - главное окно приложения. Добавим ему заголовок
        stage.setTitle("Морской Бой");
        // Панель компоновки. Позволяет размещать в себе другие элементы. В конструкторе принимает дочерние элементы
        root = new StackPane(startMenuWindow, loginWindow, loadGameWindow, addPlayerWindow);
        // Scene - это контейнер верхнего уровня для всех графических элементов
        // Класс Scene предусматривает установку корневого элемента или контейнера, который содержит все остальные элементы.
        // В нашем случае это панель компановки StackPane
        Scene scene = new Scene(root, 800, 600);
        // Установим главному окну сцену
        stage.setScene(scene);
        setWindowSize(startMenuWindow.getDefaultWindowSize());
        // Отобразим приложение на экране
        stage.show();
    }

    // Назначает функции для обработки событий
    public void assignFunctionsToHandleEvents()
    {
        // Нажатие на кнопку Закрыть программу
        startMenuWindow.setActionForExitBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                exit();
            }
        });

        // Нажатие на кнопку "Начать новую игру"
        startMenuWindow.setActionForStartNewGameBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Скрываем начальное окно
                startMenuWindow.setVisible(false);
                // Показываем окно для авторизации игроков перед началом новой игры
                loginWindow.setVisible(true);
                // Устанавливаем рекомендуемые размеры окна
                setWindowSize(loginWindow.getDefaultWindowSize());
            }
        });

        // Нажатие на кнопку "Назад" в окне loginWindow
        loginWindow.setActionForBackBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Скрываем окно для авторизации игроков перед началом новой игры
                loginWindow.setVisible(false);
                // Показываем начальное окно
                startMenuWindow.setVisible(true);
                // Устанавливаем рекомендуемые размеры окна
                setWindowSize(startMenuWindow.getDefaultWindowSize());
            }
        });

        // Нажатие на кнопку "Далее" в окне loginWindow
        loginWindow.setActionForNextBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Получаем значения полей
                String login1 = loginWindow.player1Login.getText();
                String login2 = loginWindow.player2Login.getText();
                String password1 = loginWindow.player1Pasw.getText();
                String password2 = loginWindow.player2Pasw.getText();

                // Проверяем, все ли они заполнены. Если нет, то показываем диалоговое окно с предупреждением и завершаем функцию
                if (login1.equals("") | login2.equals("") | password1.equals("") | password2.equals(""))
                {
                    showNotAllFieldsAreFilledAlert();
                    return;
                }

                // Создаём игроков на основе введёных данных
                Gamer gamer1 = new Gamer("", login1, password1);
                Gamer gamer2 = new Gamer("", login2, password2);

                try
                {
                    // Создаём новую игру с этими игроками
                    battleShip.startNewGame(gamer1, gamer2);
                    // Скрываем окно авторизации
                    loginWindow.setVisible(false);
                    // Создаёт окно для расстановки кораблей
                    createShipArrangementWindow(0);
                    loginWindow.clearInputFields();
                }
                // В случае не правильного ввода логина или пароля вызывается исключение. Показываем диалоговое окно с предупреждением
                catch (Exception e)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка!");
                    alert.setHeaderText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        // Нажатие на кнопку "Загрузить игру"
        startMenuWindow.setActionForLoadGameBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Скрываем начальное окно
                startMenuWindow.setVisible(false);
                loadGameWindow.setVisible(true);
                // Устанавливаем рекомендуемые размеры окна
                setWindowSize(loadGameWindow.getDefaultWindowSize());
                // Обновляет имена файлов на элемент listView
                loadGameWindow.addSavesNamesToListView();
            }
        });

        // Нажатие на кнопку "Назад" в окне loadGameWindow
        loadGameWindow.setActionForBackBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Скрываем окно для авторизации игроков перед началом новой игры
                loadGameWindow.setVisible(false);
                // Показываем начальное окно
                startMenuWindow.setVisible(true);
                // Устанавливаем рекомендуемые размеры окна
                setWindowSize(startMenuWindow.getDefaultWindowSize());
            }
        });

        // Нажатие на кнопку "Загрузить" в окне loadGameWindow
        loadGameWindow.setActionForLoadBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                String name = loadGameWindow.savesListView.getSelectionModel().getSelectedItem();
                if (name == null) return;
                loadGame("data/saves/" + name);
                // Скрываем окно для авторизации игроков перед началом новой игры
                loadGameWindow.setVisible(false);
            }
        });

        // Нажатие на кнопку "Игроки"
        startMenuWindow.setActionForAddGamerBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    // Если админ не добавлен, добавляем его
                    if(!battleShip.isLoginBusy("admin"))
                        // Если пользователь отказался вводить пароль админа, завершаем функцию
                        if(!setAdminPassword()) return;

                    // Запрашиваем пароль администратора
                    String password = requestPassword();
                    // Если пароль не введён, завершаем функцию
                    if (password == null || password.equals(""))
                    {
                        showWrongPasswordMessage();
                        return;
                    }

                    // Считываем с файла данные админа
                    User admin = battleShip.getGamerByLogin("admin");

                    // Если пароли совпадают, то открываем окно для добавления игроков. Иначе показываем сообщение об ошибке
                    if (!password.equals(admin.getPassword()))
                    {
                        showWrongPasswordMessage();
                        return;
                    }

                    // Скрываем начальное окно
                    startMenuWindow.setVisible(false);
                    addPlayerWindow.setVisible(true);
                    // Устанавливаем рекомендуемые размеры окна
                    setWindowSize(addPlayerWindow.getDefaultWindowSize());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        });

        // Нажатие на кнопку "Назад" в окне addGamerWindow
        addPlayerWindow.setActionForBackBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Скрываем окно для авторизации игроков перед началом новой игры
                addPlayerWindow.setVisible(false);
                // Показываем начальное окно
                startMenuWindow.setVisible(true);
                // Устанавливаем рекомендуемые размеры окна
                setWindowSize(startMenuWindow.getDefaultWindowSize());
            }
        });

        // Нажатие на кнопку "Добавить" в окне addGamerWindow
        addPlayerWindow.setActionForAddBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Получаем значения полей
                String name = addPlayerWindow.name.getText();
                String login = addPlayerWindow.login.getText();
                String password = addPlayerWindow.password.getText();

                // Проверяем, все ли они заполнены. Если нет, то показываем диалоговое окно с предупреждением и завершаем функцию
                if (login.equals("") | name.equals("") | password.equals(""))
                {
                    showNotAllFieldsAreFilledAlert();
                    return;
                }
                try
                {
                    battleShip.addUser(name, login, password);
                    addPlayerWindow.clearInputFields();
                    showPlayerAddedAlert();
                }
                catch (Exception e)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка!");
                    alert.setHeaderText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    // Показать сообщение, что пользователь добавлен
    public void showPlayerAddedAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успешно!");
        alert.setHeaderText("Пользователь успешно добавлен!");
        alert.showAndWait();
    }

    // Показать сообщение, что не все поля заполнены
    public void showNotAllFieldsAreFilledAlert()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Некоторые поля не заполнены!");
        alert.showAndWait();
    }

    // Сохраняет текущую игру
    public void saveGame()
    {
        try
        {
            // Запрашиваем имя для сохранения
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Сохранение игры");
            dialog.setHeaderText("Введите название для сохранения игры");
            dialog.setContentText("Название:");
            Optional<String> result = dialog.showAndWait();
            String name = dialog.getResult();
            // Если пользователь ничего не ввёл, то выводим сообщение и завершаем функцию
            if (name == null | name.equals(""))
            {
                showGameNotSavedAlert();
                return;
            }
            //создаем 2 потока для сериализации объекта и сохранения его в файл
            FileOutputStream outputStream = new FileOutputStream("data/saves/" + name + ".save");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            // сохраняем игру в файл
            objectOutputStream.writeObject(battleShip.currentGame);
            //закрываем потоки и освобождаем ресурсы
            outputStream.close();
            objectOutputStream.close();
            // Показываем сообщение, что игра сохранена
            showGameSavedAlert();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    // Показать сообщение, что игра не сохранена
    public void showGameNotSavedAlert()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Игра не сохранена");
        alert.setHeaderText("Игра не сохранена");
        alert.setContentText("Было введено не допустимое имя!");
        alert.showAndWait();
    }

    // Показать сообщение, что игра сохранена
    public void showGameSavedAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Игра сохранена");
        alert.setHeaderText("Игра сохранена");
        alert.showAndWait();
    }

    // Загружает сохранённую ранее игру
    public void loadGame(String gameName)
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(gameName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Game game = (Game) objectInputStream.readObject();
            battleShip.currentGame = game;
            createBattleWindow();
            battleWindow.loadShipLocation();
        }
        catch (java.io.FileNotFoundException ex)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Невозможно загрузить игру!");
            alert.setHeaderText("Невозможно загрузить игру!");
            alert.showAndWait();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    // Устанавливает размер окна приложения
    public void setWindowSize(WindowSize windowSize)
    {
        stage.setHeight(windowSize.y);
        stage.setWidth(windowSize.x);
    }

    // Завершает работу приложения
    public void exit()
    {
        // Создаём модальное окно
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Закрыть?");
        alert.setHeaderText("Вы уверены, что хотите закрыть приложение? Не сохранённые изменения будут утеряны...");
        // Показываем окно. Ждём нажатия на кнопку
        Optional<ButtonType> option = alert.showAndWait();
        // Если была нажата кнопка Да, то закрываем приложение
        if (option.get() == ButtonType.OK) stage.close();
    }

    // Создаём окно для расстановки кораблей. Показываем его
    public void createShipArrangementWindow(int playerIndex)
    {
        // В параметрах передаём индекс первого игрока и его игровое поле
        shipArrangementWindow = new ShipArrangementWindow(battleShip.currentGame.getBattleField(playerIndex), battleShip.currentGame.players[playerIndex].getName());
        // Добавляем окно в корневой макет
        root.getChildren().add(shipArrangementWindow);
        // Показываем окно расстановки кораблей
        shipArrangementWindow.setVisible(true);
        // Меняем размер окна на рекомендуемый
        setWindowSize(shipArrangementWindow.getDefaultWindowSize());
        // Добавляем окну обработчики событий
        if (playerIndex == 0) setActionForShipArrangementWindow1();
        else if (playerIndex == 1) setActionForShipArrangementWindow2();
    }

    // Удаляет окно shipArrangementWindow
    public void deleteShipArrangementWindow()
    {
        root.getChildren().remove(shipArrangementWindow);
        shipArrangementWindow = null;
    }

    // Установить обработчики событий для окна ShipArrangementWindow для 1 игрока
    public void setActionForShipArrangementWindow1()
    {
        // Нажатие на кнопку "Далее"
        shipArrangementWindow.setActionForNextBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Если расстановка кораблей ещё не окончена, выводим сообщение
                if (!battleShip.currentGame.getBattleField(0).isShipPlacementOver()) showShipsAreNotPlacedAlert();
                else
                {
                    // Удаляем старое окно
                    deleteShipArrangementWindow();
                    // Создаём другое окно
                    createShipArrangementWindow(1);
                }
            }
        });

        // Нажатие на кнопку "Назад в главное меню"
        shipArrangementWindow.setActionForBackBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (actionForBackToMenuButton())
                {
                    deleteShipArrangementWindow();
                    battleShip.currentGame = null;
                }
            }
        });
    }

    // Установить обработчики событий для окна ShipArrangementWindow для 2 игрока
    public void setActionForShipArrangementWindow2()
    {
        // Нажатие на кнопку "Далее" в окне ShipArrangementWindow
        shipArrangementWindow.setActionForNextBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // Если расстановка кораблей ещё не окончена, выводим сообщение
                if (!battleShip.currentGame.getBattleField(1).isShipPlacementOver()) showShipsAreNotPlacedAlert();
                else
                {
                    // Удаляем старое окно
                    deleteShipArrangementWindow();
                    // Создаём и показываем окно для ведения боя
                    createBattleWindow();
                }
            }
        });

        // Нажатие на кнопку "Назад в главное меню"
        shipArrangementWindow.setActionForBackBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (actionForBackToMenuButton())
                {
                    deleteShipArrangementWindow();
                    battleShip.currentGame = null;
                }
            }
        });
    }

    // Показать сообщение, что расстановка кораблей ещё не окончена
    public void showShipsAreNotPlacedAlert()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Расстановка кораблей ещё не окончена!");
        alert.showAndWait();
    }

    // Создаём окно для расстановки кораблей. Показываем его
    public void createBattleWindow()
    {
        battleWindow = new BattleWindow(battleShip.currentGame);
        // Добавляем окно в корневой макет
        root.getChildren().add(battleWindow);
        // Показываем окно
        battleWindow.setVisible(true);
        // Меняем размер окна на рекомендуемый
        setWindowSize(battleWindow.getDefaultWindowSize());
        // Добавляем окну обработчики событий
        setActionForBattleWindow();
    }

    // Удаляет окно shipArrangementWindow
    public void deleteBattleWindowWindow()
    {
        root.getChildren().remove(battleWindow);
        battleWindow = null;
    }

    // Установить обработчики событий для окна BattleWindow
    public void setActionForBattleWindow()
    {
        // Нажатие на кнопку "Сохранить"
        battleWindow.setActionForSaveBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                saveGame();
            }
        });

        // Нажатие на кнопку "Вернуться в главное меню"
        battleWindow.setActionForBackToMenuBtn(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                int gameIsOver = battleShip.currentGame.getGameIsOver();
                boolean canExit = true;
                if (gameIsOver == 0)
                {
                    canExit = actionForBackToMenuButton();
                }
                if (canExit)
                {
                    // Показываем начальное окно
                    startMenuWindow.setVisible(true);
                    // Устанавливаем рекомендуемые размеры окна
                    setWindowSize(startMenuWindow.getDefaultWindowSize());
                    // Удаляем окно для ведения боя
                    deleteBattleWindowWindow();
                    battleWindow = null;
                    battleShip.currentGame = null;
                }
            }
        });
    }

    // Действие для кнопки назад в главное меню. Если пользователь выбрал перейти в гл меню, то возвращается true
    public boolean actionForBackToMenuButton()
    {
        // Создаём модальное окно
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Вы уверены?");
        alert.setHeaderText("Вы уверены, что хотите перейти в главное меню? Не сохранённые изменения будут утеряны...");
        // Показываем окно. Ждём нажатия на кнопку
        Optional<ButtonType> option = alert.showAndWait();
        // Если была нажата кнопка Да, то закрываем приложение
        if (option.get() == ButtonType.OK)
        {
            // Показываем начальное окно
            startMenuWindow.setVisible(true);
            // Устанавливаем рекомендуемые размеры окна
            setWindowSize(startMenuWindow.getDefaultWindowSize());
            return true;
        }
        else return false;
    }

    // Установить пароль администратора
    public boolean setAdminPassword()
    {
        PasswordInputWindow dialog = new PasswordInputWindow();
        dialog.setTitle("Установка пароля");
        dialog.setHeaderText("Установите пароль администратору");
        dialog.setContentText("Пароль:");
        dialog.showAndWait();
        String password = dialog.getResult();
        if (password == null || password.equals("")) return false;

        try
        {
            battleShip.addUser("admin", "admin", password);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    // Запросить пароль
    public String requestPassword()
    {
        PasswordInputWindow dialog = new PasswordInputWindow();
        dialog.setTitle("Ввод пароля");
        dialog.setHeaderText("Введите пароль администратора");
        dialog.setContentText("Пароль:");
        dialog.showAndWait();
        return dialog.getResult();
    }

    // Показать сообщение, что пароль введён не верно
    public void showWrongPasswordMessage()
    {
        // Создаём модальное окно со значком предупреждения
        Alert alert = new Alert(Alert.AlertType.WARNING);
        // Задаём ему заголовок
        alert.setTitle("Неверный пароль!");
        // Задаём текст описания
        alert.setHeaderText("Пароль введён не верно!");
        alert.showAndWait();
    }
}


// Окно главного меню. Наследуется от модели компановки GridPane(сетка)
class StartMenuWindow extends GridPane
{
    // Графические элементы окна
    // Кнопка Начать новую игру
    private StartMenuButton startNewGameBtn = new StartMenuButton("Начать новую игру");
    //  Кнопка Добавить нового игрока
    private StartMenuButton addGamerBtn = new StartMenuButton("Игроки");
    // Кнопка Загрузить игру
    private StartMenuButton loadGameBtn = new StartMenuButton("Загрузить игру");
    // Кнопка Выход
    private StartMenuButton exitBtn = new StartMenuButton("Выход");

    // Конструктор класса
    StartMenuWindow()
    {
        // Вызываем конструктор родительского класса
        super();
        // Задаём ширину строк и столбцов
        tuneGrid();
        // Добавляем графические элементы на окно
        addElementsToGrid();
    }

    // Установить дейстивие для кнопки Начать новую игру
    public void setActionForStartNewGameBtn(EventHandler<ActionEvent> handler)
    {
        startNewGameBtn.setOnAction(handler);
    }

    // Установить дейстивие для кнопки Добавить нового игрока
    public void setActionForAddGamerBtn(EventHandler<ActionEvent> handler)
    {
        addGamerBtn.setOnAction(handler);
    }

    // Установить дейстивие для кнопки Загрузить игру
    public void setActionForLoadGameBtn(EventHandler<ActionEvent> handler)
    {
        loadGameBtn.setOnAction(handler);
    }

    // Установить дейстивие для кнопки Выход
    public void setActionForExitBtn(EventHandler<ActionEvent> handler)
    {
        exitBtn.setOnAction(handler);
    }

    // Настройка параметров строк и столбцов сетки
    private void tuneGrid()
    {
        ColumnConstraints column;
        RowConstraints row;

        // Столбец 1
        column = new ColumnConstraints();
        column.setPercentWidth(10);
        this.getColumnConstraints().add(column);

        // Столбец 2
        column = new ColumnConstraints();
        column.setPercentWidth(80);
        this.getColumnConstraints().add(column);

        // Столбец 3
        column = new ColumnConstraints();
        column.setPercentWidth(10);
        this.getColumnConstraints().add(column);

        // Строка 1
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 2
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 3
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 4
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 5
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);
    }

    // Добавляет графические элементы в окно
    private void addElementsToGrid()
    {
        Label title = new Label("Главное меню");
        title.setFont(new Font("Arial", 30));

        this.add(title, 1, 0);
        this.add(startNewGameBtn, 1, 1);
        this.add(addGamerBtn, 1, 2);
        this.add(loadGameBtn, 1, 3);
        this.add(exitBtn, 1, 4);
    }

    // Возвращает рекомендуемый размер окна
    public WindowSize getDefaultWindowSize()
    {
        return new WindowSize(400, 600);
    }
}


// Окно для авторизации игроков перед началом новой игры. Наследуется от модели компановки GridPane(сетка)
class LoginWindow extends GridPane
{
    // Графические элементы окна
    // Кнопка Далее
    private StartMenuButton next = new StartMenuButton("Далее");
    // Кнопка Вернуться в главное меню
    private StartMenuButton back = new StartMenuButton("Назад");
    // Поле для ввода логина 1 игрока
    protected TextField player1Login = new TextField();
    // Поле для ввода логина 2 игрока
    protected TextField player2Login = new TextField();
    // Поле для ввода пароля 1 игрока
    protected PasswordField player1Pasw = new PasswordField();
    // Поле для ввода пароля 2 игрока
    protected PasswordField player2Pasw = new PasswordField();

    // Конструктор класса
    LoginWindow()
    {
        // Вызываем конструктор родительского класса
        super();
        // По умолчанию скрываем окно
        setVisible(false);
        // Задаём ширину строк и столбцов
        tuneGrid();
        // Добавляем графические элементы на окно
        addElementsToGrid();
    }

    // Настройка параметров строк и столбцов сетки
    private void tuneGrid()
    {
        ColumnConstraints column;
        RowConstraints row;

        // Столбец 1
        column = new ColumnConstraints();
        column.setPercentWidth(5);
        this.getColumnConstraints().add(column);

        // Столбец 2
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        // Столбец 3
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        // Столбец 4
        column = new ColumnConstraints();
        column.setPercentWidth(10);
        this.getColumnConstraints().add(column);

        // Столбец 5
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        // Столбец 6
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        // Столбец 7
        column = new ColumnConstraints();
        column.setPercentWidth(5);
        this.getColumnConstraints().add(column);

        // Строка 1
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 2
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 3
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 4
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 5
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);
    }

    // Добавляет графические элементы в окно
    private void addElementsToGrid()
    {
        Label title = new Label("Авторизация игроков");
        title.setFont(new Font("Arial", 30));

        Label player1Lbl = new Label("Игрок 1");
        player1Lbl.setFont(new Font("Arial", 20));

        Label player2Lbl = new Label("Игрок 2");
        player2Lbl.setFont(new Font("Arial", 20));

        Label player1LoginLbl = new Label("Логин");
        Label player2LoginLbl = new Label("Логин");
        Label player1PaswLbl = new Label("Пароль");
        Label player2PaswLbl = new Label("Пароль");

        // Добавляем графические элементы на сетку
        this.add(player1Lbl, 1, 1);
        this.add(player1LoginLbl, 1, 2);
        this.add(player1PaswLbl, 1, 3);
        this.add(back, 1, 4);

        this.add(title, 2, 0, 4, 1);
        this.add(player1Login, 2, 2);
        this.add(player1Pasw, 2, 3);

        this.add(player2Lbl, 4, 1);
        this.add(player2LoginLbl, 4, 2);
        this.add(player2PaswLbl, 4, 3);

        this.add(next, 5, 4);
        this.add(player2Login, 5, 2);
        this.add(player2Pasw, 5, 3);
    }

    // Установить дейстивие для кнопки назад
    public void setActionForBackBtn(EventHandler<ActionEvent> handler)
    {
        back.setOnAction(handler);
    }

    // Установить дейстивие для кнопки далее
    public void setActionForNextBtn(EventHandler<ActionEvent> handler)
    {
        next.setOnAction(handler);
    }

    // Возвращает рекомендуемый размер окна
    public WindowSize getDefaultWindowSize()
    {
        return new WindowSize(600, 300);
    }

    // Отчистить поля ввода
    public void clearInputFields()
    {
        player1Login.clear();
        player2Login.clear();
        player1Pasw.clear();
        player2Pasw.clear();
    }
}


// Окно для расстановки кораблей
class ShipArrangementWindow extends GridPane
{
    // Игровое поле
    private BattleField field;
    // Длина корабля, который мы хотим поместить на поле. Если 0, значит такой корабль не выбран
    private int length;
    // Расположение корабля: true - горизонтально, false - вертикально
    private boolean isHorizontal;
    // Имя игрока
    private String name;

    // Графические элементы окна
    // Графическое представление поля битвы
    private BattleCard battleCard = new BattleCard();
    // Кнопка Далее
    private StartMenuButton next = new StartMenuButton("Далее");
    // Кнопка Вернуться в главное меню
    private StartMenuButton backToMenu = new StartMenuButton("В главное меню");
    // Кнопки для выбора корабля
    private ShipButton[] shipButtons = new ShipButton[4];

    // Конструктор класса
    ShipArrangementWindow(BattleField field, String playerName)
    {
        // Вызываем конструктор родительского класса
        super();
        this.field = field;
        name = playerName;
        // По умолчанию скрываем окно
        setVisible(false);
        // Задаём ширину строк и столбцов
        tuneGrid();
        // Добавляем графические элементы на окно
        addElementsToGrid();
    }

    // Настройка параметров строк и столбцов сетки
    private void tuneGrid()
    {
        ColumnConstraints column;
        RowConstraints row;

        // Столбец 1
        column = new ColumnConstraints();
        column.setPercentWidth(7);
        this.getColumnConstraints().add(column);

        // Столбец 2
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        // Столбец 3
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        // Столбец 4
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        // Столбец 5
        column = new ColumnConstraints();
        column.setPercentWidth(6);
        this.getColumnConstraints().add(column);

        // Столбец 6
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        this.getColumnConstraints().add(column);

        // Столбец 7
        column = new ColumnConstraints();
        column.setPercentWidth(7);
        this.getColumnConstraints().add(column);

        // Строка 1
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 2
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 3
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 4
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 5
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 6
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 7
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 8
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 9
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);
    }

    // Добавляет графические элементы в окно
    private void addElementsToGrid()
    {
        Label title = new Label("Расстановка кораблей игрока " + name);
        title.setFont(new Font("Arial", 30));

        shipButtons[0] = new ShipButton("Катер", 4, 1);
        shipButtons[1] = new ShipButton("Эсминец", 3, 2);
        shipButtons[2] = new ShipButton("Крейсер", 2, 3);
        shipButtons[3] = new ShipButton("Линкор", 1, 4);

        // Устанавливаем действие, при клике по кнопкам ShipButtons
        setShipButtonsClickAction();
        // Добавляем обработчик, для события передвижения мыши по ячейкам игрового поля
        setMouseMovedEvent();
        // Добавляем обработчик, для события нажатия мышью на ячейку игрового поля
        setMouseClickEvent();

        this.add(title, 1, 0, 5 ,1);
        this.add(battleCard, 1, 1, 3, 6);
        this.add(shipButtons[0], 5, 1);
        this.add(shipButtons[1], 5, 2);
        this.add(shipButtons[2], 5, 3);
        this.add(shipButtons[3], 5, 4);
        this.add(backToMenu, 1, 8);
        this.add(next, 5, 8);
    }

    // Устанавливаем действие, при клике по кнопкам ShipButtons
    public void setShipButtonsClickAction()
    {
        for (int i = 0; i < 4; i++)
        {
            shipButtons[i].setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    ShipButton shipButton = (ShipButton) event.getSource();
                    length = shipButton.shipLength;
                }
            });
        }
    }

    // Добавляем обработчик, для события передвижения мыши по ячейкам игрового поля
    public void setMouseMovedEvent()
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                // Создаём обработчик
                EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent e)
                    {
                        try
                        {
                            // Стираем следы старых зелёных кораблей
                            battleCard.repaintCells("green", "white");
                            // Если length == 0, то корабль не выбран
                            if (length == 0) return;
                            // Получаем ячейку, которая вызвала событие
                            Cell cell = (Cell) e.getSource();
                            // Создаём объект корабля
                            Ship ship = new Ship(cell.getX() - 1, cell.getY() - 1, length, isHorizontal);
                            // Ставим корабль на поле
                            if (isShipCanBeAdd(ship)) battleCard.drawShip(ship, "green");
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                };
                // Добавляем обработчик ячейке
                battleCard.addMouseMovedEventHandler(i, j, eventHandler);
            }
        }
    }

    // Добавляем обработчик, для события нажатия мышью на ячейку игрового поля
    public void setMouseClickEvent()
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                // Создаём обработчик
                EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent e)
                    {
                        try
                        {
                            // Если нажата правая кнопка мыши, то меняем ориентацию корабля
                            if (e.getButton() == MouseButton.SECONDARY)
                            {
                                if (isHorizontal) isHorizontal = false;
                                else isHorizontal = true;
                            }
                            // Если нажата левая кнопка мыши, то устанавливаем корабль на поле
                            else
                            {
                                // Если length == 0, то корабль не выбран
                                if (length == 0) return;
                                // Получаем ячейку, которой принадлежит событие
                                Cell cell = (Cell) e.getSource();
                                // Создаём корабль
                                Ship ship = new Ship(cell.getX() - 1, cell.getY() - 1, length, isHorizontal);
                                // Добавляем корабль на поле, если это возможно
                                if (isShipCanBeAdd(ship)) addShip(ship);
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                };
                // Добавляем обработчик ячейке
                battleCard.addMouseClickEventHandler(i, j, eventHandler);
            }
        }
    }

    // Установить обработку кнопки далее
    public void setActionForNextBtn(EventHandler<ActionEvent> handler)
    {
        next.setOnAction(handler);
    }

    // Установить обработку кнопки назад в меню
    public void setActionForBackBtn(EventHandler<ActionEvent> handler)
    {
        backToMenu.setOnAction(handler);
    }

    // Добаляет корабль на поле
    public void addShip(Ship ship)
    {
        try
        {
            // Добавляет корабль на игровое поле
            field.addShip(ship);
            // Отрисовывает его на поле
            battleCard.drawShip(ship, "blue");
            // Уменьшает количество кораблей данного типа на единицу. Если корабли закончились, возвращает false
            boolean isShipsRemained = shipButtons[ship.getLength() - 1].reduceCount();
            // Если корабли данного типа закончились
            if (!isShipsRemained) length = 0;
        }
        catch (Exception ex)
        {
            if (ex.getMessage().equals("Превышен лимит кораблей данного типа!"))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Внимание!");
                alert.setHeaderText("Превышен лимит кораблей данного типа!");
                alert.showAndWait();
            }
            else ex.printStackTrace();
        }
    }

    // Можно ли установить данный корабль на поле
    public boolean isShipCanBeAdd(Ship ship)
    {
        // Проверяем, что корабль умещается на поле
        if (!BattleField.isShipFitsOnField(ship)) return false;
        // Проверяем, что корабль не помещается поверх других кораблей или вплотную к ним
        if (!field.isPlatformFree(ship)) return false;
        return true;
    }

    // Возвращает рекомендуемый размер окна
    public WindowSize getDefaultWindowSize()
    {
        return new WindowSize(650, 650);
    }
}


// Окно для ведения боя
class BattleWindow extends GridPane
{
    // Класс для управления игрой
    Game game;
    // Введён ли пароль
    boolean isPasswordInput;
    // Графические элементы окна
    // Кнопка сохранить игру
    StartMenuButton saveBtn = new StartMenuButton("Сохранить игру");
    // Кнопка выйти в главное меню
    StartMenuButton backToMenuBtn = new StartMenuButton("Выйти в главное меню");
    // Графическое представление поля битвы
    BattleCard[] battleCards = new BattleCard[2];

    // Конструктор класса
    BattleWindow(Game game)
    {
        // Вызываем конструктор родительского класса
        super();
        this.game = game;
        battleCards[0] = new BattleCard();
        battleCards[1] = new BattleCard();
        // Задаём ширину строк и столбцов
        tuneGrid();
        // Добавляем графические элементы на окно
        addElementsToGrid();
        // Добавляет обработчик событию клик по ячейке
        setMouseClickHandler();
        transferRight();
        // Показываем сообщение: кто ходит следующий
        showWhoWalksNextMessage(game.players[game.getRightPlayer()].getName());
    }

    // Настройка параметров строк и столбцов сетки
    private void tuneGrid()
    {
        ColumnConstraints column;
        RowConstraints row;

        // Столбец 1
        column = new ColumnConstraints();
        column.setPercentWidth(3);
        this.getColumnConstraints().add(column);

        // Столбец 2
        column = new ColumnConstraints();
        column.setPercentWidth(15);
        this.getColumnConstraints().add(column);

        // Столбец 3
        column = new ColumnConstraints();
        column.setPercentWidth(15);
        this.getColumnConstraints().add(column);

        // Столбец 4
        column = new ColumnConstraints();
        column.setPercentWidth(15);
        this.getColumnConstraints().add(column);

        // Столбец 5
        column = new ColumnConstraints();
        column.setPercentWidth(4);
        this.getColumnConstraints().add(column);

        // Столбец 6
        column = new ColumnConstraints();
        column.setPercentWidth(15);
        this.getColumnConstraints().add(column);

        // Столбец 7
        column = new ColumnConstraints();
        column.setPercentWidth(15);
        this.getColumnConstraints().add(column);

        // Столбец 8
        column = new ColumnConstraints();
        column.setPercentWidth(15);
        this.getColumnConstraints().add(column);

        // Столбец 9
        column = new ColumnConstraints();
        column.setPercentWidth(3);
        this.getColumnConstraints().add(column);

        // Строка 1
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 2
        row = new RowConstraints();
        row.setPercentHeight(80);
        this.getRowConstraints().add(row);

        // Строка 3
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);
    }

    // Добавляет графические элементы в окно
    private void addElementsToGrid()
    {
        Label player1Lbl = new Label(game.players[0].getName());
        player1Lbl.setFont(new Font("Arial", 30));
        Label player2Lbl = new Label(game.players[1].getName());
        player2Lbl.setFont(new Font("Arial", 30));

        this.add(player1Lbl, 2, 0);
        this.add(player2Lbl, 6, 0);
        this.add(battleCards[0], 1, 1, 3, 1);
        this.add(battleCards[1], 5, 1, 3, 1);
        this.add(backToMenuBtn, 1, 2);
        this.add(saveBtn, 7, 2);
    }

    // Добавляет ячейкам BattleCard событие, которое будет срабатывать при нажатии на них мышью
    public void setMouseClickHandler()
    {
        // Проходимся по всем ячейкам карты первого игрока и каждой назначаем обработчик
        for(int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                // Обработчик
                EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>()
                {
                    // Функция, которая сработает, при нажатии
                    @Override
                    public void handle(MouseEvent e)
                    {
                        // Стрельнуть по противнику
                        makeShot((Cell) e.getSource(), 0);
                    }
                };
                // Добавляем обработчик ячейке
                battleCards[0].addMouseClickEventHandler(i, j, eventHandler);
            }
        }

        // Проходимся по всем ячейкам карты второго игрока и каждой назначаем обработчик
        for(int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                // Обработчик
                EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>()
                {
                    // Функция, которая сработает, при нажатии
                    @Override
                    public void handle(MouseEvent e)
                    {
                        // Стрельнуть по противнику
                        makeShot((Cell) e.getSource(), 1);
                    }
                };
                // Добавляем обработчик ячейке
                battleCards[1].addMouseClickEventHandler(i, j, eventHandler);
            }
        }
    }

    // Стрельнуть по ячейке cell. playerNumber - игрок стреляющий
    public void makeShot(Cell cell, int playerNumber)
    {
        try
        {
            // Если пароль не был введён
            if (!isPasswordInput)
            {
                // Запрашиваем пароль у игрока
                String password = passwordRequest(playerNumber);
                // Сравниваем с оригиналом. Если пароли не совпадают, завершаем функцию
                if (!game.players[playerNumber].getPassword().equals(password))
                {
                    // Показать сообщение, что пароль не верный
                    showWrongPasswordMessage();
                    return;
                } else isPasswordInput = true;
            }
            // Создаём снаряд с заданными координатами
            Shot shot = new Shot(cell.getX() - 1, cell.getY() - 1);
            // Делаем ход. playerNumber - игрок стреляющий
            int moveResult = game.makeMove(shot, playerNumber);
            // Игрок промахнулся
            if (moveResult == 0)
            {
                // Ставим точку на поле
                putPointOnField(cell, "точка");
                // Передаём ход другому игроку
                transferRight();
                // Пароль нужно будет ввести повторно
                isPasswordInput = false;
                // Показываем сообщение: кто ходит следующий
                showWhoWalksNextMessage(game.players[game.getRightPlayer()].getName());
            }
            // Игрок ранил
            else if (moveResult == 1)
            {
                // Ставим крестик на поле. Оставляем ход этому же игроку
                putPointOnField(cell, "крестик");
            }
            else if (moveResult == 2)
            {
                // Ставим крестик на поле. Оставляем ход этому же игроку
                putPointOnField(cell, "крестик");
                // Выводим сообщение, что корабль уничтожен
                showShipDestroyedMessage();
            }
            else if (moveResult == 3)
            {
                // Оканчиваем игру
                gameIsOver();
            }
        }
        catch (Exception ex)
        {
            if (ex.getMessage().equals("В эту координату уже стреляли ранее!"))
            {
                // Создаём модальное окно со значком предупреждения
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                // Задаём ему заголовок
                alert.setTitle("Внимание!");
                // Задаём текст описания
                alert.setHeaderText(ex.getMessage());
                alert.showAndWait();
            }
            else
            // Выводим подробности исключения
            ex.printStackTrace();
        }
    }

    // Показать сообщение: какой игрок ходит следующий
    public void showWhoWalksNextMessage(String name)
    {
        // Создаём модальное окно со значком предупреждения
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        // Задаём ему заголовок
        alert.setTitle("");
        // Задаём текст описания
        alert.setHeaderText("Право хода получает игрок " + name);
        alert.showAndWait();
    }

    // Передать право хода другому игроку
    public void transferRight()
    {
        // Деактивируем боевые карты игроков
        battleCards[0].setDisable(true);
        battleCards[1].setDisable(true);
        // Определяем, какой игрок сейчас ходит
        int rightPlayer = game.getRightPlayer();
        // Активируем игроку карту для хода
        battleCards[rightPlayer].setDisable(false);
    }

    // Запросить пароль у игрока с номером playerNumber
    public String passwordRequest(int playerNumber)
    {
        // Создаём модальное окно для ввода пароля
        PasswordInputWindow dialog = new PasswordInputWindow();
        // Задаём ему заголовок
        dialog.setTitle("Запрос пароля");
        // Задаём текст описания
        dialog.setHeaderText("Введите пароль, игрок " + game.players[playerNumber].getName());
        // Задаём текст перед формой ввода
        dialog.setContentText("Пароль:");
        // Ждём ввода пароля
        dialog.showAndWait();
        // Возвращаем текст, введёный пользователем
        return dialog.getResult();
    }

    // Показать сообщение, что пароль введён не верно
    public void showWrongPasswordMessage()
    {
        // Создаём модальное окно со значком предупреждения
        Alert alert = new Alert(Alert.AlertType.WARNING);
        // Задаём ему заголовок
        alert.setTitle("Неверный пароль!");
        // Задаём текст описания
        alert.setHeaderText("Пароль введён не верно!");
        alert.showAndWait();
    }

    // Поставить отметку на поле
    public void putPointOnField(Cell cell, String type)
    {
        // Путь к изображению
        String path;
        // Задаём путь в зависимости от типа отметки
        if (type.equals("точка")) path = "точка.jpg";
        else path="крестик.png";

        try
        {
            // Создаём объект файл и передаём ему путь к картинке точки
            File file = new File(path);
            // Преобразуем путь к файлу
            String localUrl = file.toURI().toURL().toString();
            // Создаём объект изображение, используя новый путь
            Image image = new Image(localUrl);
            // Помещаем изображение в контейнер
            ImageView imageView = new ImageView(image);
            // Добавляем контейнер в ячейку
            cell.getChildren().add(imageView);
        }
        catch (Exception e)
        {
            // Показывает детали исключения
            e.printStackTrace();
        }
    }

    // Показать сообщение, что корабль противника уничтожен
    public void showShipDestroyedMessage()
    {
        // Создаём информационое диалоговое окно, заполняем его и показываем пользователю
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Корабль уничтожен!");
        alert.setHeaderText("Корабль противника уничтожен!");
        alert.showAndWait();
    }

    // Срабатывает при окончании игры
    public void gameIsOver()
    {
        // Выводим сообщение о победе игрока
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Игра окончена!");
        alert.setHeaderText("Победил игрок " + game.players[game.getGameIsOver() - 1].getName());
        alert.showAndWait();
        // Возвращаемся в главное меню
        backToMenuBtn.fire();
    }

    // Установить обработку кнопки сохранить игру
    public void setActionForSaveBtn(EventHandler<ActionEvent> handler)
    {
        saveBtn.setOnAction(handler);
    }

    // Установить обработку кнопки вернуться в меню
    public void setActionForBackToMenuBtn(EventHandler<ActionEvent> handler)
    {
        backToMenuBtn.setOnAction(handler);
    }

    // Возвращает рекомендуемый размер окна
    public WindowSize getDefaultWindowSize()
    {
        return new WindowSize(850, 550);
    }

    // Загружает расположение кораблей на поле
    public void loadShipLocation()
    {
        try
        {
            for (int player = 0; player < 2; player++)
            {
                // Номер игрового поля
                int fieldIndex;
                // Номер игрового поля противоположен номеру игрока
                if (player == 0) fieldIndex = 1; else fieldIndex = 0;
                // Получаем игровое поле для игрока player
                BattleField battleField = game.getBattleField(fieldIndex);
                // Получаем ячейки поля
                Cell cell[][] = battleCards[player].getCells();

                // Проходимся по ячейкам
                for (int i = 0; i < 10; i++)
                    for (int j = 0; j < 10; j++)
                    {
                        Shot shot = new Shot(i, j);
                        // Если в эту ячейку стреляли ранее, то продолжаем
                        if (battleField.isShotAtThisPoint(shot))
                            // Если в этой ячейке был корабль, то в ячейку ставим крестик
                            if (battleField.isShipLocatedInCell(i, j))
                                putPointOnField(cell[i][j], "крестик");
                            // Если в этой ячейке не было корабля, то в ячейку ставим точку
                            else putPointOnField(cell[i][j], "точка");
                    }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}


// Кнопка с изменением некоторых параметров. Наследуется от стандартной кнопки
class StartMenuButton extends Button
{
    StartMenuButton(String name)
    {
        // Вызываем конструктор базового класса
        super(name);
        // Говорим кнопке, заполнять все достпуное ей пространство
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);
        // Устанавливаем отступ для кнопки
        GridPane.setMargin(this, new Insets(10));
    }
}


// Кнопка для окна расстановки кораблей. Нужна, чтобы выбрать, какой корабль ставить на поле
class ShipButton extends Button
{
    // Название корабля
    String name;
    // Количество экземпляров корабля
    int count;
    // Длина корабля
    int shipLength;

    // Конструктор класса
    ShipButton(String name, int count, int shipLength)
    {
        super();
        this.name = name;
        this.count = count;
        this.shipLength = shipLength;
        // Устанавливаем текст кнопки
        setText(getCountName());

        // Кнопка заполняет все отведённое ей пространство
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);
        // установим отступ для кнопки в 10 единиц
        GridPane.setMargin(this, new Insets(10));
    }

    // Уменьшает количество кораблей на единицу. Переименовывает кнопку, так как в названии кнопки содержится количество не расставленных кораблей
    public boolean reduceCount()
    {
        // Уменьшаем количество на единицу
        count--;
        // Меняем название кнопки
        setText(getCountName());
        // Если количество доступных кораблей равно 0, блокируем кнопку. Возвращаем false
        if (count == 0)
        {
            setDisable(true);
            return false;
        }
        else return true;
    }

    // Возвращает имя кнопки с количеством оставшихся кораблей
    public String getCountName()
    {
        return name + " (ост. " + count + ")";
    }
}


// Графическое представление поля битвы
class BattleCard extends GridPane
{
    // Клетки на поле
    private Cell[][] cells = new Cell[10][10];

    // Конструктор класса
    BattleCard()
    {
        // Вызываем конструктор родительского класса
        super();
        // Задаём ширину строк и столбцов
        tuneGrid();
        // Добавление заголовков к игровому полю(буквы по горизонтали, цифры по вертикали)
        addTitle();
        // Добавление клеток на поле
        addCells();
    }

    // Настройка параметров строк и столбцов сетки
    private void tuneGrid()
    {
        for (int i = 0; i < 11; i++)
        {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(9);
            this.getColumnConstraints().add(column);
        }

        for (int i = 0; i < 11; i++)
        {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(9);
            this.getRowConstraints().add(row);
        }
    }

    // Добавление заголовков к игровому полю(буквы по горизонтали, цифры по вертикали)
    private void addTitle()
    {
        for (int i = 1; i < 11; i++)
        {
            this.add(new Label(Integer.toString(i)), 0, i);
        }

        String[] chars = new String[] {"А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К"};

        for (int i = 1; i < 11; i++)
        {
            this.add(new Label(chars[i-1]), i, 0);
        }
    }

    // Добавление клеток на поле
    private void addCells()
    {
        for (int i = 1; i < 11; i++)
        {
            for (int j = 1; j < 11; j++)
            {
                Cell cell = new Cell(i, j);
                cells[i-1][j-1] = cell;
                this.add(cell, i, j);
            }
        }
    }

    // Добавить ячейке действие, которое будет срабатывать, когда мышь будет двигаться над ячейкой
    public void addMouseMovedEventHandler(int x, int y, EventHandler<MouseEvent> eventHandler)
    {
        cells[x][y].addEventFilter(MouseEvent.MOUSE_MOVED, eventHandler);
    }

    // Добавить ячейке действие при клике по ней мышью
    public void addMouseClickEventHandler(int x, int y, EventHandler<MouseEvent> eventHandler)
    {
        cells[x][y].addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
    }

    // Перерисовать ячейки с цветом currentColor в цвет futureColor
    public void repaintCells(String currentColor, String futureColor)
    {
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                String style = cells[i][j].getStyle();
                if (style.equals("-fx-background-color:" + currentColor))
                {
                    cells[i][j].setStyle("-fx-background-color:" + futureColor);
                    // Рисуем границу у данной ячейки
                    cells[i][j].setStyle("-fx-border-color:black");
                }
            }
        }
    }

    // Нарисовать корабль цветом color на игровом поле
    public void drawShip(Ship ship, String color)
    {
        int x = ship.getX();
        int y = ship.getY();
        int length = ship.getLength();

        // Отрисовка происходит по разному для кораблей расположеных горизонтально и вертикально
        if (ship.getIsHorizontal())
        {
            for(int i = x; i < x + length; i++)
            {
                cells[i][y].setStyle("-fx-background-color:" + color);
            }
        }
        else
        {
            for(int i = y; i < y + length; i++)
            {
                cells[x][i].setStyle("-fx-background-color:" + color);
            }
        }
    }

    public Cell[][] getCells()
    {
        return cells;
    }
}


// Клетка на игровом поле
class Cell extends StackPane
{
    // Координаты клетки
    private int x;
    private int y;

    Cell(int x, int y)
    {
        super();
        this.x = x;
        this.y = y;
        // Устанавливаем цвет фона клетки белым
        setStyle("-fx-background-color:white");
        // Вокруг клетки рисуем границу
        setStyle("-fx-border-color:black");
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}


// Размер окна
class WindowSize
{
    // Ширина окна
    int x;
    // Длина окна
    int y;

    WindowSize(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}


// Окно для загрузки игры
class LoadGameWindow extends GridPane
{
    // Графические элементы окна
    // Кнопка Далее
    private StartMenuButton load = new StartMenuButton("Далее");
    // Кнопка Вернуться в главное меню
    private StartMenuButton back = new StartMenuButton("Назад");
    // Список сохранённых игр
    protected ListView<String> savesListView = new ListView<String>();

    // Конструктор класса
    LoadGameWindow()
    {
        // Вызываем конструктор родительского класса
        super();
        // По умолчанию скрываем окно
        setVisible(false);
        // Задаём ширину строк и столбцов
        tuneGrid();
        // Добавляем графические элементы на окно
        addElementsToGrid();

    }

    // Настройка параметров строк и столбцов сетки
    private void tuneGrid()
    {
        ColumnConstraints column;
        RowConstraints row;

        // Столбец 1
        column = new ColumnConstraints();
        column.setPercentWidth(5);
        this.getColumnConstraints().add(column);

        // Столбец 2
        column = new ColumnConstraints();
        column.setPercentWidth(30);
        this.getColumnConstraints().add(column);

        // Столбец 3
        column = new ColumnConstraints();
        column.setPercentWidth(30);
        this.getColumnConstraints().add(column);

        // Столбец 4
        column = new ColumnConstraints();
        column.setPercentWidth(30);
        this.getColumnConstraints().add(column);

        // Столбец 5
        column = new ColumnConstraints();
        column.setPercentWidth(5);
        this.getColumnConstraints().add(column);

        // Строка 1
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 2
        row = new RowConstraints();
        row.setPercentHeight(60);
        this.getRowConstraints().add(row);

        // Строка 3
        row = new RowConstraints();
        row.setPercentHeight(10);
        this.getRowConstraints().add(row);

        // Строка 4
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);
    }

    // Добавляет графические элементы в окно
    private void addElementsToGrid()
    {
        Label title = new Label("Загрузка игры");
        title.setFont(new Font("Arial", 30));

        this.add(title, 1, 0, 3, 1);
        this.add(savesListView, 1, 1, 3, 1);
        this.add(back, 1, 3);

        this.add(load, 3, 3);
    }

    // Возвращает список файлов сохранений игры
    public File[] getSavesList()
    {
        File dir = new File("data/saves/");
        return dir.listFiles();
    }

    // Добавляет имена файлов на элемент listView
    public void addSavesNamesToListView()
    {
        savesListView.getItems().clear();
        File[] files = getSavesList();
        for (File file : files)
        {
            savesListView.getItems().add(file.getName());
        }
    }

    // Установить дейстивие для кнопки назад
    public void setActionForBackBtn(EventHandler<ActionEvent> handler)
    {
        back.setOnAction(handler);
    }

    // Установить дейстивие для кнопки далее
    public void setActionForLoadBtn(EventHandler<ActionEvent> handler)
    {
        load.setOnAction(handler);
    }

    // Возвращает рекомендуемый размер окна
    public WindowSize getDefaultWindowSize()
    {
        return new WindowSize(600, 400);
    }
}


// Окно для загрузки игры
class AddPlayerWindow extends GridPane
{
    // Графические элементы окна
    // Кнопка Далее
    private StartMenuButton add = new StartMenuButton("Добавить");
    // Кнопка Вернуться в главное меню
    private StartMenuButton back = new StartMenuButton("Назад");
    // Поле для ввода имени
    protected TextField name = new TextField();
    // Поле для ввода логина
    protected TextField login = new TextField();
    // Поле для ввода пароля
    protected PasswordField password = new PasswordField();

    // Конструктор класса
    AddPlayerWindow()
    {
        // Вызываем конструктор родительского класса
        super();
        // По умолчанию скрываем окно
        setVisible(false);
        // Задаём ширину строк и столбцов
        tuneGrid();
        // Добавляем графические элементы на окно
        addElementsToGrid();
    }

    // Настройка параметров строк и столбцов сетки
    private void tuneGrid()
    {
        ColumnConstraints column;
        RowConstraints row;

        // Столбец 1
        column = new ColumnConstraints();
        column.setPercentWidth(10);
        this.getColumnConstraints().add(column);

        // Столбец 2
        column = new ColumnConstraints();
        column.setPercentWidth(30);
        this.getColumnConstraints().add(column);

        // Столбец 3
        column = new ColumnConstraints();
        column.setPercentWidth(10);
        this.getColumnConstraints().add(column);

        // Столбец 4
        column = new ColumnConstraints();
        column.setPercentWidth(40);
        this.getColumnConstraints().add(column);

        // Столбец 5
        column = new ColumnConstraints();
        column.setPercentWidth(10);
        this.getColumnConstraints().add(column);

        // Строка 1
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 2
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 3
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 4
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);

        // Строка 5
        row = new RowConstraints();
        row.setPercentHeight(20);
        this.getRowConstraints().add(row);
    }

    // Добавляет графические элементы в окно
    private void addElementsToGrid()
    {
        Label title = new Label("Добавление нового игрока");
        title.setFont(new Font("Arial", 30));
        Label nameLbl = new Label("Имя");
        Label loginLbl = new Label("Логин");
        Label passwordLbl = new Label("Пароль");

        this.add(title, 1, 0, 3, 1);
        this.add(nameLbl, 1, 1);
        this.add(loginLbl, 1, 2);
        this.add(passwordLbl, 1, 3);
        this.add(back, 1, 4);

        this.add(name, 3, 1);
        this.add(login, 3, 2);
        this.add(password, 3, 3);
        this.add(add, 3, 4);
    }

    // Установить дейстивие для кнопки назад
    public void setActionForBackBtn(EventHandler<ActionEvent> handler)
    {
        back.setOnAction(handler);
    }

    // Установить дейстивие для кнопки далее
    public void setActionForAddBtn(EventHandler<ActionEvent> handler)
    {
        add.setOnAction(handler);
    }

    // Возвращает рекомендуемый размер окна
    public WindowSize getDefaultWindowSize()
    {
        return new WindowSize(600, 400);
    }

    // Отчистить поля ввода
    public void clearInputFields()
    {
        name.clear();
        login.clear();
        password.clear();
    }
}


// Окно ввода пароля
class PasswordInputWindow extends Stage
{
    // Графические элементы окна
    // Подсказка
    Label header = new Label();
    // Подсказка
    Label content = new Label();
    // Поле для ввода пароля
    PasswordField password = new PasswordField();
    // Модель компоновки макета
    GridPane root = new GridPane();;
    // Кнопка продолжить
    StartMenuButton ok = new StartMenuButton("ОK");
    // Кнопка назад
    StartMenuButton back = new StartMenuButton("Cansel");

    // Конструктор класса
    PasswordInputWindow()
    {
        // Вызываем конструктор базового класса
        super();
        // На сцене размещаются все GUI компоненты
        Scene scene = new Scene(root, 300, 150);
        // Установим главному окну сцену
        setScene(scene);
        // Задаём ширину строк и столбцов
        tuneGrid();
        // Добавляем графические элементы на окно
        addElementsToGrid();
        // Выставляем модальность окна. Оно будет блокировать все другие окна приложения
        initModality(Modality.APPLICATION_MODAL);
    }

    // Настройка параметров строк и столбцов сетки
    private void tuneGrid()
    {
        ColumnConstraints column;
        RowConstraints row;

        // Столбец 1
        column = new ColumnConstraints();
        column.setPercentWidth(5);
        root.getColumnConstraints().add(column);

        // Столбец 2
        column = new ColumnConstraints();
        column.setPercentWidth(30);
        root.getColumnConstraints().add(column);

        // Столбец 3
        column = new ColumnConstraints();
        column.setPercentWidth(30);
        root.getColumnConstraints().add(column);

        // Столбец 4
        column = new ColumnConstraints();
        column.setPercentWidth(30);
        root.getColumnConstraints().add(column);

        // Столбец 5
        column = new ColumnConstraints();
        column.setPercentWidth(5);
        root.getColumnConstraints().add(column);

        // Строка 1
        row = new RowConstraints();
        row.setPercentHeight(30);
        root.getRowConstraints().add(row);

        // Строка 2
        row = new RowConstraints();
        row.setPercentHeight(35);
        root.getRowConstraints().add(row);

        // Строка 3
        row = new RowConstraints();
        row.setPercentHeight(35);
        root.getRowConstraints().add(row);
    }

    // Добавляет графические элементы в окно
    private void addElementsToGrid()
    {
        EventHandler<ActionEvent> handler =  new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                close();
            }
        };

        ok.setOnAction(handler);
        back.setOnAction(handler);

        root.add(header, 1, 0, 3, 1);
        root.add(content, 1, 1);
        root.add(password, 2, 1, 2, 1);
        root.add(ok, 2, 2);
        root.add(back, 3, 2);
    }

    public void setHeaderText(String s)
    {
        header.setText(s);
    }

    public void setContentText(String s)
    {
        content.setText(s);
    }

    public String getResult()
    {
        return password.getText();
    }
}