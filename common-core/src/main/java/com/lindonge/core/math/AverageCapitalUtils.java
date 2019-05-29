package com.lindonge.core.math;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 等额本金
 * 是指一种贷款的还款方式，是在还款期内把贷款数总额等分，每月偿还同等数额的本金和剩余贷款在该月所产生的利息，这样由于每月的还款本金额固定，
 * 而利息越来越少，借款人起初还款压力较大，但是随时间的推移每月还款数也越来越少。
 */
public class AverageCapitalUtils {

    /**
     * 等额本金计算获取还款方式为等额本金的每月偿还本金和利息
     * <p>
     * 公式：每月偿还本金=(贷款本金÷还款月数)+(贷款本金-已归还本金累计额)×月利率
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还本金和利息, 不四舍五入，直接截取小数点最后两位
     */
    public static Map<Integer, Double> getPerMonthPrincipalInterest(double invest, double yearRate, int totalMonth) {
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        // 每月本金
        double monthPri = getPerMonthPrincipal(invest, totalMonth);
        // 获取月利率
        double monthRate = yearRate / 12;
        monthRate = new BigDecimal(monthRate).setScale(6, BigDecimal.ROUND_DOWN).doubleValue();
        for (int i = 1; i <= totalMonth; i++) {
            double monthRes = monthPri + (invest - monthPri * (i - 1)) * monthRate;
            monthRes = new BigDecimal(monthRes).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            map.put(i, monthRes);
        }
        return map;
    }

    /**
     * 等额本金计算获取还款方式为等额本金的每月偿还利息
     * <p>
     * 公式：每月应还利息=剩余本金×月利率=(贷款本金-已归还本金累计额)×月利率
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还利息
     */
    public static Map<Integer, Double> getPerMonthInterest(double invest, double yearRate, int totalMonth) {
        Map<Integer, Double> inMap = new HashMap<Integer, Double>();
        double principal = getPerMonthPrincipal(invest, totalMonth);
        Map<Integer, Double> map = getPerMonthPrincipalInterest(invest, yearRate, totalMonth);
        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            BigDecimal principalBigDecimal = new BigDecimal(principal);
            BigDecimal principalInterestBigDecimal = new BigDecimal(entry.getValue());
            BigDecimal interestBigDecimal = principalInterestBigDecimal.subtract(principalBigDecimal);
            interestBigDecimal = interestBigDecimal.setScale(2, BigDecimal.ROUND_DOWN);
            inMap.put(entry.getKey(), interestBigDecimal.doubleValue());
        }
        return inMap;
    }

    /**
     * 等额本金计算获取还款方式为等额本金的每月偿还本金
     * <p>
     * 公式：每月应还本金=贷款本金÷还款月数
     *
     * @param invest     总借款额（贷款本金）
     * @param totalMonth 还款总月数
     * @return 每月偿还本金
     */
    public static double getPerMonthPrincipal(double invest, int totalMonth) {
        BigDecimal monthIncome = new BigDecimal(invest).divide(new BigDecimal(totalMonth), 2, BigDecimal.ROUND_DOWN);
        return monthIncome.doubleValue();
    }

    /**
     * 等额本金计算获取还款方式为等额本金的总利息
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 总利息
     */
    public static double getInterestCount(double invest, double yearRate, int totalMonth) {
        BigDecimal count = new BigDecimal(0);
        Map<Integer, Double> mapInterest = getPerMonthInterest(invest, yearRate, totalMonth);

        for (Map.Entry<Integer, Double> entry : mapInterest.entrySet()) {
            count = count.add(new BigDecimal(entry.getValue()));
        }
        return count.doubleValue();
    }


    /**
     * 计算等额本金还款
     * 贷款本金100万，贷款期限30年。每月还款本金=100万/360月，每月还款利息=剩余贷款金额*月利率。所以每月利息递减。
     *
     * @param principal 贷款总额
     * @param months    贷款期限
     * @param rate      贷款利率
     * @return
     */
    public static String[] calculateEqualPrincipal(double principal, int months, double rate) {
        ArrayList<String> data = new ArrayList<String>();
        double monthRate = rate / (100 * 12);//月利率
        double prePrincipal = principal / months;//每月还款本金
        double firstMonth = prePrincipal + principal * monthRate;//第一个月还款金额
        double decreaseMonth = prePrincipal * monthRate;//每月利息递减
        double interest = (months + 1) * principal * monthRate / 2;//还款总利息
        double totalMoney = principal + interest;//还款总额
        data.add(String.valueOf(totalMoney));//还款总额
        data.add(String.valueOf(principal));//贷款总额
        data.add(String.valueOf(interest));//还款总利息
        data.add(String.valueOf(firstMonth));//首月还款金额
        data.add(String.valueOf(decreaseMonth));//每月递减利息
        data.add(String.valueOf(months));//还款期限
        return data.toArray(new String[data.size()]);
    }

    /**
     * 一次性提前还款计算(等额本金)
     *
     * @param principal 贷款总额
     * @param months    贷款期限
     * @param payTimes  已还次数
     * @param rate      贷款利率
     * @return
     */
    public static String[] calculateEqualPrincipal(double principal, int months, int payTimes, double rate) {
        ArrayList<String> data = new ArrayList<String>();
        double monthRate = rate / (100 * 12);//月利率
        double prePrincipal = principal / months;//每月还款本金
        double firstMonth = prePrincipal + principal * monthRate;//第一个月还款金额
        double decreaseMonth = prePrincipal * monthRate;//每月利息递减
        double interest = (months + 1) * principal * monthRate / 2;//还款总利息
        double totalMoney = principal + interest;//还款总额
        double payLoan = prePrincipal * payTimes;//已还本金
        double payInterest = (principal * payTimes - prePrincipal * (payTimes - 1) * payTimes / 2) * monthRate;//已还利息
        double payTotal = payLoan + payInterest;//已还总额
        double totalPayAhead = (principal - payLoan) * (1 + monthRate);//提前还款金额（剩余本金加上剩余本金当月利息）
        double saveInterest = totalMoney - payTotal - totalPayAhead;
        data.add(String.valueOf(totalMoney));//原还款总额
        data.add(String.valueOf(principal));//贷款总额
        data.add(String.valueOf(interest));//原还款总利息
        data.add(String.valueOf(firstMonth));//原首月还款金额
        data.add(String.valueOf(decreaseMonth));//原每月递减利息
        data.add(String.valueOf(payTotal));//已还总金额
        data.add(String.valueOf(payLoan));//已还本金
        data.add(String.valueOf(payInterest));//已还利息
        data.add(String.valueOf(totalPayAhead));//一次还清支付金额
        data.add(String.valueOf(saveInterest));//节省利息
        data.add(String.valueOf(0));//剩余还款期限
        return data.toArray(new String[data.size()]);
    }

    /**
     *  部分提前还款计算(等额本金、月供不变)
     * @param principal      贷款总额
     * @param months         贷款期限
     * @param aheadPrincipal 提前还款金额
     * @param payTimes       已还次数
     * @param rate           贷款利率
     * @return
     */
    public static String[] calculateEqualPrincipalApart(double principal, int months, double aheadPrincipal, int payTimes, double rate) {
        ArrayList<String> data = new ArrayList<String>();
        double monthRate = rate / (100 * 12);//月利率
        double prePrincipal = principal / months;//每月还款本金
        double firstMonth = prePrincipal + principal * monthRate;//第一个月还款金额
        double decreaseMonth = prePrincipal * monthRate;//每月利息递减
        double interest = (months + 1) * principal * monthRate / 2;//还款总利息
        double totalMoney = principal + interest;//还款总额
        double payLoan = prePrincipal * payTimes;//已还本金
        double payInterest = (principal * payTimes - prePrincipal * (payTimes - 1) * payTimes / 2) * monthRate;//已还利息
        double payTotal = payLoan + payInterest;//已还总额
        double aheadTotalMoney = (principal - payLoan) *  monthRate+aheadPrincipal+prePrincipal;//提前还款金额
        double leftLoan = principal - aheadPrincipal - payLoan-prePrincipal;//剩余金额
        int leftMonth = (int) Math.floor(leftLoan / prePrincipal);
        double newPrePrincipal = leftLoan / leftMonth;//新的每月还款本金
        double newFirstMonth = newPrePrincipal + leftLoan * monthRate;//新的第一个月还款金额
        double newDecreaseMonth = newPrePrincipal * monthRate;//新的每月利息递减
        double leftInterest = (leftMonth + 1) * leftLoan * monthRate / 2;//还款总利息
        double leftTotalMoney = leftLoan + leftInterest;//还款总额
        double saveInterest = totalMoney-payTotal-aheadTotalMoney-leftTotalMoney;
        data.add(String.valueOf(totalMoney));//原还款总额
        data.add(String.valueOf(principal));//贷款总额
        data.add(String.valueOf(interest));//原还款总利息
        data.add(String.valueOf(firstMonth));//原还首月还款金额
        data.add(String.valueOf(decreaseMonth));//原每月递减利息
        data.add(String.valueOf(payTotal));//已还总金额
        data.add(String.valueOf(payLoan));//已还本金
        data.add(String.valueOf(payInterest));//已还利息
        data.add(String.valueOf(aheadTotalMoney));//提前还款总额
        data.add(String.valueOf(leftTotalMoney));//剩余还款总额
        data.add(String.valueOf(leftInterest));//剩余还款总利息
        data.add(String.valueOf(newFirstMonth));//剩余首月还款金额
        data.add(String.valueOf(newDecreaseMonth));//剩余月递减利息
        data.add(String.valueOf(saveInterest));//节省利息
        data.add(String.valueOf(leftMonth));//剩余还款期限
        return data.toArray(new String[data.size()]);
    }

    /**
     * 部分提前还款计算(等额本金、期限不变)
     *
     * @param principal      贷款总额
     * @param months         贷款期限
     * @param aheadPrincipal 提前还款金额
     * @param payTimes       已还次数
     * @param rate           贷款利率
     * @return
     */
    public static String[] calculateEqualPrincipalApart2(double principal, int months, double aheadPrincipal, int payTimes, double rate) {
        ArrayList<String> data = new ArrayList<String>();
        double monthRate = rate / (100 * 12);//月利率
        double prePrincipal = principal / months;//每月还款本金
        double firstMonth = prePrincipal + principal * monthRate;//第一个月还款金额
        double decreaseMonth = prePrincipal * monthRate;//每月利息递减
        double interest = (months + 1) * principal * monthRate / 2;//还款总利息
        double totalMoney = principal + interest;//还款总额
        double payLoan = prePrincipal * payTimes;//已还本金
        double payInterest = (principal * payTimes - prePrincipal * (payTimes - 1) * payTimes / 2) * monthRate;//已还利息
        double payTotal = payLoan + payInterest;//已还总额
        double aheadTotalMoney = (principal - payLoan) * monthRate + aheadPrincipal + prePrincipal;//提前还款金额
        int leftMonth = months - payTimes - 1;
        double leftLoan = principal - aheadPrincipal - payLoan - prePrincipal;
        double newPrePrincipal = leftLoan / leftMonth;//新的每月还款本金
        double newFirstMonth = newPrePrincipal + leftLoan * monthRate;//新的第一个月还款金额
        double newDecreaseMonth = newPrePrincipal * monthRate;//新的每月利息递减
        double leftInterest = (leftMonth + 1) * leftLoan * monthRate / 2;//还款总利息
        double leftTotalMoney = leftLoan + leftInterest;//还款总额
        double saveInterest = totalMoney - payTotal - aheadTotalMoney - leftTotalMoney;
        data.add(String.valueOf(totalMoney));//原还款总额
        data.add(String.valueOf(principal));//贷款总额
        data.add(String.valueOf(interest));//原还款总利息
        data.add(String.valueOf(firstMonth));//原还首月还款金额
        data.add(String.valueOf(decreaseMonth));//原每月递减利息
        data.add(String.valueOf(payTotal));//已还总金额
        data.add(String.valueOf(payLoan));//已还本金
        data.add(String.valueOf(payInterest));//已还利息
        data.add(String.valueOf(aheadTotalMoney));//提前还款总额
        data.add(String.valueOf(leftTotalMoney));//剩余还款总额
        data.add(String.valueOf(leftInterest));//剩余还款总利息
        data.add(String.valueOf(newFirstMonth));//剩余首月还款金额
        data.add(String.valueOf(newDecreaseMonth));//剩余月递减利息
        data.add(String.valueOf(saveInterest));//节省利息
        data.add(String.valueOf(months));//剩余还款期限
        return data.toArray(new String[data.size()]);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        double invest = 10000; // 本金
        int month = 12;
        double yearRate = 0.15; // 年利率
        Map<Integer, Double> getPerMonthPrincipalInterest = getPerMonthPrincipalInterest(invest, yearRate, month);
        System.out.println("等额本金---每月本息：" + getPerMonthPrincipalInterest);
        double benjin = getPerMonthPrincipal(invest, month);
        System.out.println("等额本金---每月本金:" + benjin);
        Map<Integer, Double> mapInterest = getPerMonthInterest(invest, yearRate, month);
        System.out.println("等额本金---每月利息:" + mapInterest);

        double count = getInterestCount(invest, yearRate, month);
        System.out.println("等额本金---总利息：" + count);
    }
}
