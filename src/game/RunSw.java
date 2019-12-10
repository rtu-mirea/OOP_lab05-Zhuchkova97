package game;
import com.sun.glass.ui.Window;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.JOptionPane;

import javax.swing.*;




public class RunSw
{

    public static void run(String[] args)
    {

        //Создадим окно и установим заголовок
        final JFrame window = new JFrame("Морской бой");

        //Подключаем иконку из корня папки проекта
        ImageIcon img = new ImageIcon("java.png");
        window.setIconImage(img.getImage());

        //Событие "закрыть" при нажатии по крестику окна
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Текстовое поле
        JTextField textField = new JTextField();
        textField.setBackground(Color.WHITE);
        textField.setColumns(14);


        //Создадим панель
        JPanel panel = new JPanel();

        //Создадим кнопки
        JButton minButton = new JButton("Свернуть");
        JButton maxButton = new JButton("Растянуть");
        JButton normalButton = new JButton("Оригинал");
        JButton exitButton = new JButton("Выход");
        JButton helloButton = new JButton("Приветствие");
        JButton rulleButton = new JButton("Правила игры");
        JButton Button1= new JButton("Оставьте отзыв");
        String[] items = {
                "плохо",
                "очень плохо",
                "ужасно"
        };
        JComboBox comboBox = new JComboBox(items);




        //Событие для кнопки "Свернуть"
        minButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Действие
                window.setState(JFrame.ICONIFIED);
            }
        });

        //Событие для кнопки "Растянуть"
        maxButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Действие
                window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });


        //Событие для кнопки "Оригинал"
        normalButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Действие
                window.setExtendedState(JFrame.NORMAL);
            }
        });


        //Событие для кнопки "Выход"
        exitButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Действие
                window.setVisible(false);
                System.exit(0);
            }
        });

        //Событие для кнопки "Hello"
        helloButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Действие
                textField.setText("Привет, Игрок!");
            }
        });

        rulleButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Действие
                WindowRull WR= new WindowRull();
            }
        });

        Button1.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                //Действие
                JOptionPane.showMessageDialog(null,"Спасибо за отзыв! Вы ответили: " + (String)comboBox.getSelectedItem());
            }
        });




        //Добавим кнопки и поля на панель
        panel.add(minButton);
        panel.add(maxButton);
        panel.add(normalButton);
        panel.add(exitButton);
        panel.add(textField);
        panel.add(helloButton);
        panel.add(rulleButton);
        panel.add(Button1);
        panel.add(comboBox);

        //Добавим панель в окно
        window.getContentPane().add(panel);

        window.pack();

        //Разместим программу по центру
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    //Запускаем
    public static void main(String[] args)
    {
        run(args);
    }

}

class WindowRull extends JFrame{
    JLabel label1= new JLabel("Правила игры");
    GridLayout panel1 = new GridLayout(2,1,5,15);
    JTextArea pane1= new JTextArea("Как играть в морской бой на листочке\n" +
            "Проще всего использовать тетрадные листы в клетку. Если взять альбомные, придется расчерчивать поле с помощью линейки. Предварительно нужно договориться и о буквенных обозначениях. Некоторые игроки предпочитают использовать слова из 10 не повторяющихся букв (например, «Республика» или «Снегурочка»).\n" +
            "Создание поля\n" +
            "Можно воспользоваться листочком из тетради в клеточку либо самостоятельно начертить по 2 площадки размером 10*10 для каждого из играющих. В первой размещают собственную флотилию, вторая требуется для фиксации сделанных выстрелов, попаданий в боевые единицы противника." +
            "В классической версии у каждого игрока флот состоит из 10 боевых единиц, отличающихся длиной. Всего должно быть:\n" +
            "шлюпки — 4 судна по 1 клетке;\n" +
            "эсминцы — 3 из 2 клеточек;\n" +
            "крейсеры — 2 фигуры из 3;\n" +
            "линкор — 1 корабль из 4.\n" +
            "Фигуры не должны пересекаться или соприкасаться, в том числе и по диагонали. По умолчанию корабли рисуют в виде прямой палки, но по договоренности участников их можно изгибать или делать квадрат. В таком случае вычислить положение линкора противника будет сложнее." +
            "Очередность ходов\n" +
            "Можно договориться, кто ходит первым, или тянуть жребий. Право хода передается второму играющему после промаха. Если человек попадает в цель, он продолжает ходить." +
            "Игровые стратегии и тактики\n" +
            "Часто крупные единицы располагают на одной половине поля, а мелкие — на другой. Хотя крейсера, линкоры и эсминцы будет нетрудно обнаружить, потопить катер противнику окажется непросто. Лучше, если однопалубные суда будут находиться далеко друг от друга. Так соперник потратит много времени на их поиск, за счет чего появится возможность отыграться.\n" +
            "Эффективны и простреливания по диагоналям. С их помощью находят крупные корабли. Опытные любители морского боя учитывают это при размещении собственной флотилии.\n" +
            "Игрок может расположить все единицы по краям. Тогда противнику придется потратить много выстрелов на проверку пустой территории внутри квадрата.\n" +
            "Тактику соперника нужно учитывать при размещении своих кораблей. Если противник новичок, лучше воздержаться от расположения боевых единиц в угловых клетках. Начинающие игроки часто проверяют их первыми. Если играете с опытным, можно спрятать 1-2 мелких судна в углах. Из-за сформировавшегося шаблона углы проверять он станет последними." +
            "Любые ходы соперника следует фиксировать на своем поле. Каждое свое действие указывать на втором. Отмечаются не только попадания, но и неудачные ходы. Это позволит не выстрелить повторно в пустой квадрат, предотвратит ошибки и возможные разногласия." +
            "Когда корабль противника будет потоплен, следует отмечать окружающие его клетки точками, как уже обстрелянные. По правилам игры располагать в них суда запрещается, поэтому тратить ходы на проверку необязательно. Выгоднее всего потопить линкор, поскольку при этом откроются сразу 18 клеток. Если поставить крупный корабль около стенки, количество открытых ячеек сократится до 10.",8,10);
    WindowRull()
    {
        super();
        setSize(800,600);
        setVisible(true);
        add(label1);
        add(new JScrollPane(pane1));
        pane1.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pane1.setLineWrap(true);
//        getContentPane().add(panel1);
        setLayout(panel1);
        pack();

    }



}

