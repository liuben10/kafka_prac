import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class Generator {
    static int DEFAULT_MIN = 0;
    static int DEFAULT_MAX = 10000;
    static int DEFAULT_LENGTH = 10;
    static int DEFAULT_PRECISION = 10;
    static List<String> DEFAULT_EMAIL_PROVIDERS = new ArrayList<>(Arrays.asList(
            "google.com",
            "yahoo.com"
    ));

    public static HashMap<String, Object> generate(Schema s) {
        HashMap<String, Object> sample = new HashMap<>();
        for (RowSpec row : s.specs) {
            sample.put(row.name, generateRow(row));
        }
        return sample;
    }

    static Object generateRow(RowSpec row) {
        if (row.type == Type.INT) {
            Optional<String> minSet = getExtraSetting(row, "min");
            Optional<String> maxSet = getExtraSetting(row, "max");
            Integer min = minSet.isPresent() ? Integer.parseInt(minSet.get()) : DEFAULT_MIN;
            Integer max = maxSet.isPresent() ? Integer.parseInt(maxSet.get()) : DEFAULT_MAX;
            assert(max >= min);
            Random r = new Random();
            return r.nextInt(max - min) + min;
        } else if (row.type == Type.STRING) {
            return genString(row);
            // Double
        } else  {
            Optional<String> minSet = getExtraSetting(row, "min");
            Optional<String> maxSet = getExtraSetting(row, "max");
            int min = minSet.map(Integer::parseInt).orElseGet(() -> DEFAULT_MIN);
            int max = maxSet.map(Integer::parseInt).orElseGet(() -> DEFAULT_MAX);
            assert(max > min);
            if (max-1 > min) {
                max -= 1;
            }
            Random r = new Random();
            int intpart = r.nextInt(max - min) + min;
            double dpart = Math.random();
            Optional<String> precSet = getExtraSetting(row, "precision");
            int prec = precSet.map(Integer::parseInt).orElseGet(() -> DEFAULT_PRECISION);
            BigDecimal bd = new BigDecimal(dpart);
            bd.setScale(prec, RoundingMode.FLOOR);
            return intpart + bd.doubleValue();
        }
    }

    static String genPhone(Optional<String> country) {
        StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append("+1 "); // TODO Use country code lookup
        Random r = new Random();
        for(int i = 0; i < 10; i++) {
            phoneNumber.append(r.nextInt(9));
            if (i == 3 || i  == 6) {
                phoneNumber.append("-");
            }
        }
        return phoneNumber.toString();
    }

    static String genString(RowSpec row) {
        Optional<String> formatSet = getExtraSetting(row, "format");
        if (formatSet.isPresent()) {
            if (formatSet.get().equals("phone")) {
                Optional<String> country = getExtraSetting(row, "country");
                return genPhone(country);
            } else if (formatSet.get().equals("email")) {
                Optional<String> possibleEmails = getExtraSetting(row, "emails");
                List<String> emailSet = parseEmails(possibleEmails);
                return genEmail(emailSet, row);
            } else {
                return genDefaultString(row);
            }
        } else {
            return genDefaultString(row);
        }
    }

    private static String genDefaultString(RowSpec row) {
        Optional<String> lengthSetting = getExtraSetting(row, "length");
        int length = lengthSetting.map(Integer::parseInt).orElseGet(() -> DEFAULT_LENGTH);
        Optional<String> charsetSetting = getExtraSetting(row, "charset");
        Random r = new Random();
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < length; i++) {
            if (charsetSetting.isPresent()) {
                if (charsetSetting.get().equals("alpha")) {
                    int caseSel = r.nextInt(2);
                    if (caseSel == 0) {
                        res.append((char) (r.nextInt(26) + 65));
                    } else {
                        res.append((char) (r.nextInt(26) + 97));
                    }
                } else if (charsetSetting.equals("alphanumeric")) {
                    int caseSel = r.nextInt(3);
                    if (caseSel == 0) {
                        res.append((char) (r.nextInt(26) + 65));
                    } else if (caseSel == 1){
                        res.append((char) (r.nextInt(26) + 97));
                    } else {
                        res.append((char) (r.nextInt(10) + 48));
                    }
                } else if (charsetSetting.equals("numeric")) {
                    res.append((char) (r.nextInt(10) + 48));
                } else {
                    res.append((char) r.nextInt(127));
                }
            } else {
                res.append((char) r.nextInt(127));
            }
        }
        return res.toString();
    }

    private static String genEmail(List<String> emailSet, RowSpec row) {
        Random r = new Random();
        int ridx = r.nextInt(emailSet.size());
        return genDefaultString(row) + "@" + emailSet.get(ridx);
    }

    private static List<String> parseEmails(Optional<String> possibleEmails) {
        if (possibleEmails.isPresent()) {
            return Arrays.asList(possibleEmails.get().split(","));
        } else {
            return DEFAULT_EMAIL_PROVIDERS;
        }
    }

    static Optional<String> getExtraSetting(RowSpec row, String setting) {
        if (row.extra.isPresent() && row.extra.get().containsKey(setting)) {
            return Optional.of(row.extra.get().get(setting));
        } else {
            return Optional.empty();
        }
    }
}
