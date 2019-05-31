package com.lingdonge.core.captcha;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * 验证码录入窗口，弹出界面让用户录入验证码。
 * 使用方法：String input = new CaptchaFrame(img).getUserInput();
 * Created by kyle on 17/4/12.
 */
public class CaptchaFrame {

    JFrame frame;

    JPanel panel;

    JTextField input;

    int inputWidth = 100;

    BufferedImage img;

    String userInput = null;

    /**
     * 构造函数
     *
     * @param img
     */
    public CaptchaFrame(BufferedImage img) {
        this.img = img;
    }

    /**
     * 显示验证码输入框，并反馈用户输入的验证码
     *
     * @return
     */
    public String getUserInput() {
        frame = new JFrame("输入验证码");
        final int imgWidth = img.getWidth() < 300 ? img.getWidth() : 300;//超过300显示300
        final int imgHeight = img.getHeight() < 300 ? img.getHeight() : 300;//超过300显示300
        int width = imgWidth * 2 + inputWidth * 2;
        int height = imgHeight * 2 + 50;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int startx = (dim.width - width) / 2;
        int starty = (dim.height - height) / 2;
        frame.setBounds(startx, starty, width, height);
        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, imgWidth * 2, imgHeight * 2, null);
            }
        };
        panel.setLayout(null);
        container.add(panel);
        input = new JTextField(6);
        input.setBounds(imgWidth * 2, 0, inputWidth, imgHeight * 2);
        panel.add(input);
        JButton btn = new JButton("登录");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userInput = input.getText().trim();
                synchronized (CaptchaFrame.this) {
                    CaptchaFrame.this.notify();
                }
            }
        });
        btn.setBounds(imgWidth * 2 + inputWidth, 0, inputWidth, imgHeight * 2);
        panel.add(btn);
        frame.setVisible(true);
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        frame.dispose();
        return userInput;
    }
}