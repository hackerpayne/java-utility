package com.lindonge.core.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 等额本息还款
 * 也称定期付息，即借款人每月按相等的金额偿还贷款本息，其中每月贷款利息按月初剩余贷款本金计算并逐月结清。把按揭贷款的本金总额与利息总额相加，
 * 然后平均分摊到还款期限的每个月中。作为还款人，每个月还给银行固定金额，但每月还款额中的本金比重逐月递增、利息比重逐月递减。
 * <p>
 * 每期偿还本息数=P×[r(1+t)*n次方]/（1+r）*n次方-1]
 * 式中，P为贷款额，r为贷款年利率，n为贷款期(月)数。
 */
public class AverageCapitalPlusInterestUtils {

    /**
     * 等额本息计算：获取还款方式为等额本息的每月偿还本金和利息
     * <p>
     * 公式：每月偿还本息=〔贷款本金×月利率×(1＋月利率)＾还款月数〕÷〔(1＋月利率)＾还款月数-1〕
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalmonth 还款总月数
     * @return 每月偿还本金和利息, 不四舍五入，直接截取小数点最后两位
     */
    public static BigDecimal getPerMonthPrincipalInterest(double invest, double yearRate, int totalmonth) {
        double monthRate = yearRate / 12;
        BigDecimal monthIncome = new BigDecimal(invest)
                .multiply(new BigDecimal(monthRate * Math.pow(1 + monthRate, totalmonth)))
                .divide(new BigDecimal(Math.pow(1 + monthRate, totalmonth) - 1), 2, BigDecimal.ROUND_UP);
        //return monthIncome.doubleValue();
        return monthIncome;
    }

    /**
     * 等额本息计算：获取还款方式为等额本息的每月偿还利息
     * <p>
     * 公式：每月偿还利息=贷款本金×月利率×〔(1+月利率)^还款月数-(1+月利率)^(还款月序号-1)〕÷〔(1+月利率)^还款月数-1〕
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalmonth 还款总月数
     * @return 每月偿还利息
     */
    public static Map<Integer, BigDecimal> getPerMonthInterest(double invest, double yearRate, int totalmonth) {
        Map<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();
        double monthRate = yearRate / 12;
        BigDecimal monthInterest;
        for (int i = 1; i < totalmonth + 1; i++) {
            BigDecimal multiply = new BigDecimal(invest).multiply(new BigDecimal(monthRate));
            BigDecimal sub = new BigDecimal(Math.pow(1 + monthRate, totalmonth))
                    .subtract(new BigDecimal(Math.pow(1 + monthRate, i - 1)));
            monthInterest = multiply.multiply(sub).divide(new BigDecimal(Math.pow(1 + monthRate, totalmonth) - 1), 2,
                    BigDecimal.ROUND_DOWN);
            monthInterest = monthInterest.setScale(2, BigDecimal.ROUND_DOWN);
            map.put(i, monthInterest);
        }
        return map;
    }

    /**
     * 等额本息计算获取还款方式为等额本息的每月偿还本金
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalmonth 还款总月数
     * @return 每月偿还本金
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipal(double invest, double yearRate, int totalmonth) {
        double monthRate = yearRate / 12;
        BigDecimal monthIncome = new BigDecimal(invest)
                .multiply(new BigDecimal(monthRate * Math.pow(1 + monthRate, totalmonth)))
                .divide(new BigDecimal(Math.pow(1 + monthRate, totalmonth) - 1), 2, BigDecimal.ROUND_DOWN);
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, totalmonth);
        Map<Integer, BigDecimal> mapPrincipal = new HashMap<Integer, BigDecimal>();

        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            mapPrincipal.put(entry.getKey(), monthIncome.subtract(entry.getValue()));
        }
        return mapPrincipal;
    }

    /**
     * 等额本息计算获取还款方式为等额本息的总利息
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalmonth 还款总月数
     * @return 总利息
     */
    public static double getInterestCount(double invest, double yearRate, int totalmonth) {
        BigDecimal count = new BigDecimal(0);
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, totalmonth);

        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            count = count.add(entry.getValue());
        }
        return count.doubleValue();
    }

    /**
     * 应还本金总和
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalmonth 还款总月数
     * @return 应还本金总和
     */
    public static double getPrincipalInterestCount(double invest, double yearRate, int totalmonth) {
        double monthRate = yearRate / 12;
        BigDecimal perMonthInterest = new BigDecimal(invest)
                .multiply(new BigDecimal(monthRate * Math.pow(1 + monthRate, totalmonth)))
                .divide(new BigDecimal(Math.pow(1 + monthRate, totalmonth) - 1), 2, BigDecimal.ROUND_DOWN);
        BigDecimal count = perMonthInterest.multiply(new BigDecimal(totalmonth));
        count = count.setScale(2, BigDecimal.ROUND_DOWN);
        return count.doubleValue();
    }

    /**
     * 计算等额本息还款
     * 每月还款本金+利息一样。贷款本金100万，贷款期限30年，贷款利息91万元（近似值），那么月供191万元/360月。
     *
     * @param principal 贷款总额
     * @param months    贷款期限
     * @param rate      贷款利率
     * @return
     */
    public static String[] calculateEqualPrincipalAndInterest(double principal, int months, double rate) {
        ArrayList<String> data = new ArrayList<String>();
        double monthRate = rate / (100 * 12);//月利率
        double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
        double totalMoney = preLoan * months;//还款总额
        double interest = totalMoney - principal;//还款总利息
        data.add(String.valueOf(totalMoney));//还款总额
        data.add(String.valueOf(principal));//贷款总额
        data.add(String.valueOf(interest));//还款总利息
        data.add(String.valueOf(preLoan));//每月还款金额
        data.add(String.valueOf(months));//还款期限
        return data.toArray(new String[data.size()]);
    }

    /**
     * 一次性提前还款计算（等额本息）
     * 扣除之前每月还款的本金（前期还款时，利息占月供很大的比例），剩余贷款金额加上一个月利息即支付金额。
     *
     * @param principal 贷款总额
     * @param months    贷款期限
     * @param payTimes  已还次数
     * @param rate      贷款利率
     * @return
     */
    public static String[] calculateEqualPrincipalAndInterest(double principal, int months, int payTimes, double rate) {
        ArrayList<String> data = new ArrayList<String>();
        double monthRate = rate / (100 * 12);//月利率
        double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
        double totalMoney = preLoan * months;//还款总额
        double interest = totalMoney - principal;//还款总利息
        double leftLoan = principal * Math.pow(1 + monthRate, payTimes) - preLoan * (Math.pow(1 + monthRate, payTimes) - 1) / monthRate;//n个月后欠银行的钱
        double payLoan = principal - leftLoan;//已还本金
        double payTotal = preLoan * payTimes;//已还总金额
        double payInterest = payTotal - payLoan;//已还利息
        double totalPayAhead = leftLoan * (1 + monthRate);//剩余一次还清
        double saveInterest = totalMoney - payTotal - totalPayAhead;
        data.add(String.valueOf(totalMoney));//原还款总额
        data.add(String.valueOf(principal));//贷款总额
        data.add(String.valueOf(interest));//原还款总利息
        data.add(String.valueOf(preLoan));//原还每月还款金额
        data.add(String.valueOf(payTotal));//已还总金额
        data.add(String.valueOf(payLoan));//已还本金
        data.add(String.valueOf(payInterest));//已还利息
        data.add(String.valueOf(totalPayAhead));//一次还清支付金额
        data.add(String.valueOf(saveInterest));//节省利息
        data.add(String.valueOf(0));//剩余还款期限
        return data.toArray(new String[data.size()]);
    }

    /**
     * 部分提前还款计算（等额本息、月供不变）
     * 贷款100万，提前还款50万。首次还款：17年8月，提前还款：18年6月。已还次数10次是截止到18年5月。剩余贷款，保持月供不变（大致维持不变），后续需还款114期（9年零6月），所以提前还款后还款日期：18年7月~27年12月。
     *
     * @param principal      贷款总额
     * @param months         贷款期限
     * @param aheadPrincipal 提前还款金额
     * @param payTimes       已还次数
     * @param rate           贷款利率
     * @return
     */
    public static String[] calculateEqualPrincipalAndInterestApart(double principal, int months, double aheadPrincipal, int payTimes, double rate) {
        ArrayList<String> data = new ArrayList<String>();
        double monthRate = rate / (100 * 12);//月利率
        double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
        double totalMoney = preLoan * months;//还款总额
        double interest = totalMoney - principal;//还款总利息
        double leftLoanBefore = principal * Math.pow(1 + monthRate, payTimes) - preLoan * (Math.pow(1 + monthRate, payTimes) - 1) / monthRate;//提前还款前欠银行的钱
        double leftLoan = principal * Math.pow(1 + monthRate, payTimes + 1) - preLoan * (Math.pow(1 + monthRate, payTimes + 1) - 1) / monthRate - aheadPrincipal;//提前还款后欠银行的钱
        double payLoan = principal - leftLoanBefore;//已还本金
        double payTotal = preLoan * payTimes;//已还总金额
        double payInterest = payTotal - payLoan;//已还利息
        double aheadTotalMoney = aheadPrincipal + preLoan;//提前还款总额
        //计算剩余还款期限
        int leftMonth = (int) Math.floor(Math.log(preLoan / (preLoan - leftLoan * monthRate)) / Math.log(1 + monthRate));
        double newPreLoan = (leftLoan * monthRate * Math.pow((1 + monthRate), leftMonth)) / (Math.pow((1 + monthRate), leftMonth) - 1);//剩余贷款每月还款金额
        double leftTotalMoney = newPreLoan * leftMonth;//剩余还款总额
        double leftInterest = leftTotalMoney - (leftLoan - aheadPrincipal);
        double saveInterest = totalMoney - aheadTotalMoney - leftTotalMoney - payTotal;
        data.add(String.valueOf(totalMoney));//原还款总额
        data.add(String.valueOf(principal));//贷款总额
        data.add(String.valueOf(interest));//原还款总利息
        data.add(String.valueOf(preLoan));//原还每月还款金额
        data.add(String.valueOf(payTotal));//已还总金额
        data.add(String.valueOf(payLoan));//已还本金
        data.add(String.valueOf(payInterest));//已还利息
        data.add(String.valueOf(aheadTotalMoney));//提前还款总额
        data.add(String.valueOf(leftTotalMoney));//剩余还款总额
        data.add(String.valueOf(leftInterest));//剩余还款总利息
        data.add(String.valueOf(newPreLoan));//剩余每月还款金额
        data.add(String.valueOf(saveInterest));//节省利息
        data.add(String.valueOf(leftMonth));//剩余还款期限
        return data.toArray(new String[data.size()]);
    }

    /**
     * 部分提前还款计算（等额本息、期限不变）
     *
     * @param principal      贷款总额
     * @param months         贷款期限
     * @param aheadPrincipal 提前还款金额
     * @param payTimes       已还次数
     * @param rate           贷款利率
     * @return
     */
    public static String[] calculateEqualPrincipalAndInterestApart2(double principal, int months, double aheadPrincipal, int payTimes, double rate) {
        ArrayList<String> data = new ArrayList<String>();
        double monthRate = rate / (100 * 12);//月利率
        double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
        double totalMoney = preLoan * months;//还款总额
        double interest = totalMoney - principal;//还款总利息
        double leftLoanBefore = principal * Math.pow(1 + monthRate, payTimes) - preLoan * (Math.pow(1 + monthRate, payTimes) - 1) / monthRate;//提前还款前欠银行的钱
        double leftLoan = principal * Math.pow(1 + monthRate, payTimes + 1) - preLoan * (Math.pow(1 + monthRate, payTimes + 1) - 1) / monthRate;//提前还款后银行的钱
        double payLoan = principal - leftLoanBefore;//已还本金
        double payTotal = preLoan * payTimes;//已还总金额
        double payInterest = payTotal - payLoan;//已还利息
        double aheadTotalMoney = preLoan + aheadPrincipal;//下个月还款金额
        double newPreLoan = ((leftLoan - aheadPrincipal) * monthRate * Math.pow((1 + monthRate), months - payTimes - 1)) / (Math.pow((1 + monthRate), months - payTimes - 1) - 1);//下个月起每月还款金额
        double leftTotalMoney = newPreLoan * (months - payTimes);
        double leftInterest = leftTotalMoney - (leftLoan - aheadPrincipal);
        double saveInterest = totalMoney - payTotal - aheadTotalMoney - leftTotalMoney;
        data.add(String.valueOf(totalMoney));//原还款总额
        data.add(String.valueOf(principal));//贷款总额
        data.add(String.valueOf(interest));//原还款总利息
        data.add(String.valueOf(preLoan));//原还每月还款金额
        data.add(String.valueOf(payTotal));//已还总金额
        data.add(String.valueOf(payLoan));//已还本金
        data.add(String.valueOf(payInterest));//已还利息
        data.add(String.valueOf(aheadTotalMoney));//提前还款总额
        data.add(String.valueOf(leftTotalMoney));//剩余还款总额
        data.add(String.valueOf(leftInterest));//剩余还款总利息
        data.add(String.valueOf(newPreLoan));//剩余每月还款金额
        data.add(String.valueOf(saveInterest));//节省利息
        data.add(String.valueOf(months));//剩余还款期限
        return data.toArray(new String[data.size()]);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        double invest = 38988; // 本金
        int month = 12;
        double yearRate = 0.15; // 年利率

        BigDecimal perMonthPrincipalInterest = getPerMonthPrincipalInterest(invest, yearRate, month);
        System.out.println("等额本息---每月还款本息：" + perMonthPrincipalInterest);
        System.out.println("等额本息---每月还款本息：" + getPerMonthPrincipalInterest(invest, yearRate, 3));
        System.out.println("等额本息---每月还款本息：" + getPerMonthPrincipalInterest(invest, yearRate, 6));
        System.out.println("等额本息---每月还款本息：" + getPerMonthPrincipalInterest(invest, yearRate, 9));
        System.out.println("等额本息---每月还款本息：" + getPerMonthPrincipalInterest(invest, yearRate, 12));
        System.out.println("等额本息---每月还款本息：" + getPerMonthPrincipalInterest(invest, yearRate, 15));
        System.out.println("等额本息---每月还款本息：" + getPerMonthPrincipalInterest(invest, yearRate, 18));

        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, month);
        System.out.println("等额本息---每月还款利息：" + mapInterest);

        Map<Integer, BigDecimal> mapPrincipal = getPerMonthPrincipal(invest, yearRate, month);
        System.out.println("等额本息---每月还款本金：" + mapPrincipal);

        double count = getInterestCount(invest, yearRate, month);
        System.out.println("等额本息---总利息：" + count);

        double principalInterestCount = getPrincipalInterestCount(invest, yearRate, month);
        System.out.println("等额本息---应还本息总和：" + principalInterestCount);

    }
}