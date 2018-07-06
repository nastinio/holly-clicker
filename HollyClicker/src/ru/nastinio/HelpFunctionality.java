package ru.nastinio;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelpFunctionality {
    //Вспомогательные методы, вынесенные в отдельный класс,
    //чтобы не захломлять основные рабочие классы

    //Методы для работы со строкой для получения даты рождения
    public int getBDigit(String input, ConstVK typeDigit){
        //Получает на вход строку с содержанием нужной даты и возвращает ее
        //Если произошла ошибка в получении числа, вернет 0

        //Ожидаемы input'ы для работы метода:
        //String bdayAndMonthLink ="https://vk.com/search?c[section]=people&c[bday]=11&c[bmonth]=2";
        //String byearLink = "/search?c[section]=people&c[byear]=1995";
        int result=0;

        Pattern patternBDay = Pattern.compile("\\[bday\\]=");
        Pattern patternBMonth = Pattern.compile("&c\\[bmonth\\]=");
        Pattern patternBYear = Pattern.compile("\\[byear\\]=");

        Pattern pattern = Pattern.compile("");
        switch (typeDigit){
            case BMONTH:
                pattern = patternBMonth;
                break;
            case BYEAR:
                pattern = patternBYear;
                break;
            case BDAY:
                //Сначала подготовим строку input, отрезав от нее кусок с месяцем
                Matcher matcher = patternBMonth.matcher(input);
                if (matcher.find()) {
                    input = input.substring(0,matcher.start());
                }else{
                    System.out.println("No match found");
                }
                pattern = patternBDay;
                break;
        }
        result = getLastIntByPattern(input,pattern);
        return result;
    }

    private int getLastIntByPattern(String input, Pattern pattern){
        //Вспомогательный метод для getBDigit
        //Отрезает от input хвост после паттерна
        //Если произошла ошибка в получении числа, вернет 0
        int result=0;
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String resultStr = input.substring(matcher.end(),input.length());
            try{
                result = Integer.parseInt(resultStr);
                //System.out.println("Получили число: "+result+" для паттерна "+pattern);
            }catch (NumberFormatException e){
                System.out.println("Ошибка в преобразовании строки "+resultStr+" в int");
            }
        }else{
            System.out.println("No match found");
        }

        return result;
    }

    private void testGetBDigit(){
        //Просто примеры вызовов, пущай хранятся
        String bdayAndMonthLink ="https://vk.com/search?c[section]=people&c[bday]=11&c[bmonth]=2";
        String byearLink = "/search?c[section]=people&c[byear]=1995";

        HelpFunctionality hp = new HelpFunctionality();
        int bday = hp.getBDigit(bdayAndMonthLink,ConstVK.BDAY);
        int bmonth = hp.getBDigit(bdayAndMonthLink,ConstVK.BMONTH);
        int byear = hp.getBDigit(byearLink,ConstVK.BYEAR);

        System.out.printf("Дата рождения: %d.%d.%d",bday,bmonth,byear);
    }
}
