package com.lingdonge.http.faker;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Random;

@Slf4j
public class FakeMobileHelper {

    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            System.out.println("New Mobile Num:" + getFakeMobile());
        }
    }

    /**
     * 随机生成手机号码前3位
     *
     * @return
     */
    public static String getFakeMobile() {
        int[] mobileStart = {139, 138, 137, 136, 135, 134, 159, 158, 157, 150, 151, 152, 188, 130, 131, 132, 156, 155, 133, 153, 189, 180, 177, 176};
        Random r = new Random();
        ArrayList<Integer> mobileList = new ArrayList<>();
        for (int i = 0; i < mobileStart.length; i++) {
            mobileList.add(mobileStart[i]);
        }

        // 生成后8位
        String temp = "";
        for (int i = 0; i < 8; i++) {
            // 4位后面加-隔开
//            if(i==4){
//                temp += "-";
//            }
            temp += r.nextInt(10);
        }

        return mobileList.get(r.nextInt(mobileList.size())) + temp;
    }


}
