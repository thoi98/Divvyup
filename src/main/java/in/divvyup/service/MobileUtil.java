package in.divvyup.service;

import in.divvyup.exception.InvalidRequestException;

public class MobileUtil {
    /**
     * @param phone number in format 91-9742496645
     * @return Standard phone number in format +919742496645
     */
    public static String getStandardPhoneNumber(String phone) {
        if (!phone.contains("-")) {
            throw new InvalidRequestException("Invalid phone number");
        }
        return "+" + phone.trim().replaceAll("-", "");
    }
}
