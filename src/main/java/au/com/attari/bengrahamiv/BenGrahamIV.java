package au.com.attari.bengrahamiv;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BenGrahamIV {

    private BigDecimal priceEarningBase = BenGrahamClassicalParams.PRICE_EARNING_BASE;

    private String companyCode;

    private String exchange;
    private BigDecimal earningPerShare;
    private BigDecimal growthRate;
    private BigDecimal growthRateMultiplier = BenGrahamClassicalParams.GROWTH_RATE_MULTIPLIER;

    private BigDecimal currentCorporateBoundYield = BenGrahamClassicalParams.CURRENT_CORPORATE_BOUND_YIELD; // average for past year ???

    private BigDecimal averageCorporateBoundYield = BenGrahamClassicalParams.AVERAGE_CORPORATE_BOUND_YIELD; // Based on BG calc

    private static String pattern = "[0-9]*\\.?[0-9][0-9]";




    public static void main(String[] args) {

        if(args.length != 4) {
            System.out.println("usage: java BenGrahamIV <Company Code> <exchange> <Earning Per Share> <growth rate>");
            System.exit(-1);
        }

        BenGrahamIV bGIV = new BenGrahamIV();
        bGIV.setCompanyCode(args[0]);
        bGIV.setExchange(args[1]);

        if(!args[2].matches(getPattern())) {
            System.err.println("Earning per Share must have currency format");
            System.exit(-1);
        }

        // Classical
        bGIV.setEarningPerShare(new BigDecimal(args[2]));
        bGIV.setGrowthRate(new BigDecimal(args[3]));
        BigDecimal iv = bGIV.calculateBenGrahamIV();
        BigDecimal ivMS = iv.multiply(BenGrahamClassicalParams.MARGIN_OF_SAFETY).setScale(2, RoundingMode.HALF_UP);
        System.out.println(String.format("Classical Intrinsic Value for %s.%s is %s and buy price with margin of safety is %s",
                bGIV.getCompanyCode(), bGIV.getExchange(), iv, ivMS));

        // Modern
        bGIV.setGrowthRateMultiplier(BenGrahamClassicalParams.GROWTH_RATE_MULTIPLIER);
        bGIV.setCurrentCorporateBoundYield(BenGrahamModernParams.CURRENT_CORPORATE_BOUND_YIELD);
        bGIV.setAverageCorporateBoundYield(BenGrahamModernParams.AVERAGE_CORPORATE_BOUND_YIELD);
        bGIV.setPriceEarningBase(BenGrahamModernParams.PRICE_EARNING_BASE);
        bGIV.setGrowthRate(new BigDecimal(args[3]));
        iv = bGIV.calculateBenGrahamIV();
        ivMS = iv.multiply(BenGrahamModernParams.MARGIN_OF_SAFETY).setScale(2, RoundingMode.HALF_UP);
        System.out.println(String.format("Modern Intrinsic Value for %s.%s is %s  and buy price with margin of safety is %s",
                bGIV.getCompanyCode(), bGIV.getExchange(), iv, ivMS));

        System.exit(0);
    }

    private BigDecimal calculateBenGrahamIV() {

        BigDecimal gR = getGrowthRateMultiplier().multiply(getGrowthRate());
        BigDecimal pR = gR.add(getPriceEarningBase());
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP) ;
        BigDecimal iv = getEarningPerShare().multiply(pR).multiply(getAverageCorporateBoundYield()).divide(getCurrentCorporateBoundYield(), mc).round(mc);
        iv = iv.setScale(2, RoundingMode.HALF_UP);
        return iv;
    }

    public BigDecimal getPriceEarningBase() {
        return priceEarningBase;
    }

    public void setPriceEarningBase(BigDecimal priceEarningBase) {
        this.priceEarningBase = priceEarningBase;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public BigDecimal getEarningPerShare() {
        return earningPerShare;
    }

    public void setEarningPerShare(BigDecimal earningPerShare) {
        this.earningPerShare = earningPerShare;
    }

    public BigDecimal getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(BigDecimal growthRate) {
        this.growthRate = growthRate;
    }

    public BigDecimal getGrowthRateMultiplier() {
        return growthRateMultiplier;
    }

    public void setGrowthRateMultiplier(BigDecimal growthRateMultiplier) {
        this.growthRateMultiplier = growthRateMultiplier;
    }

    public BigDecimal getCurrentCorporateBoundYield() {
        return currentCorporateBoundYield;
    }

    public void setCurrentCorporateBoundYield(BigDecimal currentCorporateBoundYield) {
        this.currentCorporateBoundYield = currentCorporateBoundYield;
    }

    public static String getPattern() {
        return pattern;
    }

    public BigDecimal getAverageCorporateBoundYield() {
        return averageCorporateBoundYield;
    }

    public void setAverageCorporateBoundYield(BigDecimal averageCorporateBoundYield) {
        this.averageCorporateBoundYield = averageCorporateBoundYield;
    }

    public static void setPattern(String pattern) {
        BenGrahamIV.pattern = pattern;
    }
}