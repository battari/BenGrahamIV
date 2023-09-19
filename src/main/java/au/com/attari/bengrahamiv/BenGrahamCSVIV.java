package au.com.attari.bengrahamiv;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.List;

public class BenGrahamCSVIV {

    private BigDecimal priceEarningBase = BenGrahamClassicalParams.PRICE_EARNING_BASE;

    private Stream<String> file;

    public static void main(String[] args) {

        if(args.length != 1) {
            System.out.println("usage: java BenGrahamCSVIV <file path>");
            System.exit(-1);
        }

        BenGrahamCSVIV bGCIV = new BenGrahamCSVIV();
        System.out.println(args[0]);
        Path path = Path.of(args[0]);
        boolean exists = Files.exists(path);
        if(!exists) {
            System.out.println("usage: java BenGrahamCSVIV <valid file path>");
            System.exit(-1);
        }
        try {
            Stream<String> strStream = Files.lines(path);
            bGCIV.setFile(strStream);
        } catch(Exception e) {
            System.out.println("file content: <Company Code>,<exchange>,<Earning Per Share>,<growth rate>,<current price>");
            System.exit(-1);
        }

        bGCIV.process();

        System.exit(0);
    }

    public void process() {
        this.getFile().forEach(item->processLine(item));
    }

    public void processLine(String line) {
        String[] args = line.split(",", -1);

        // Make the following methods in

        BenGrahamIV bGIV = new BenGrahamIV();
        bGIV.setCompanyCode(args[0]);
        bGIV.setExchange(args[1]);

        // Classical
        bGIV.setEarningPerShare(new BigDecimal(args[2]));
        bGIV.setGrowthRate(new BigDecimal(args[3]));
        bGIV.setCurrentPrice(new BigDecimal(args[4]));
        BigDecimal iv = bGIV.calculateBenGrahamIV();
        BigDecimal ivMS = iv.multiply(BenGrahamClassicalParams.MARGIN_OF_SAFETY).setScale(2, RoundingMode.HALF_UP);
        Boolean buyable = ivMS.compareTo(bGIV.getCurrentPrice()) > 0;
        System.out.println(String.format("Classical Intrinsic Value for %s.%s is %s. Buy price with margin of safety is %s. Is it buyable? %s",
                bGIV.getCompanyCode(), bGIV.getExchange(), iv, ivMS, buyable));

        // Modern
        bGIV.setGrowthRateMultiplier(BenGrahamClassicalParams.GROWTH_RATE_MULTIPLIER);
        bGIV.setCurrentCorporateBoundYield(BenGrahamModernParams.CURRENT_CORPORATE_BOUND_YIELD);
        bGIV.setAverageCorporateBoundYield(BenGrahamModernParams.AVERAGE_CORPORATE_BOUND_YIELD);
        bGIV.setPriceEarningBase(BenGrahamModernParams.PRICE_EARNING_BASE);
        bGIV.setGrowthRate(new BigDecimal(args[3]));
        iv = bGIV.calculateBenGrahamIV();
        ivMS = iv.multiply(BenGrahamModernParams.MARGIN_OF_SAFETY).setScale(2, RoundingMode.HALF_UP);
        buyable = ivMS.compareTo(bGIV.getCurrentPrice()) > 0;
        System.out.println(String.format("Modern Intrinsic Value for %s.%s is %s. Buy price with margin of safety is %s. Is it buyable? %s",
                bGIV.getCompanyCode(), bGIV.getExchange(), iv, ivMS, buyable));

    }

    public Stream<String> getFile() {
        return file;
    }

    public void setFile(Stream<String> file) {
        this.file = file;
    }
}