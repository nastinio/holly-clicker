package ru.nastinio.clientVK;

import ru.nastinio.Enums.ConstVK;
import ru.nastinio.Exceptions.SearchIDException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelpFunctionalityVK {
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
                    //System.out.println("No match found");
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
        //System.out.println("Получили на вход в getLastIntByPattern: "+input);
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
            //System.out.println("No match found");
        }

        return result;
    }

    public String getYearStr(String byearLink){
        int year = getBDigit(byearLink, ConstVK.BYEAR);
        if(year == 0){
            return "0000";
        }else{
            return String.valueOf(year);
        }
    }
    public String getDayAndMonthStr(String bdayAndMonthLink){
        int day = getBDigit(bdayAndMonthLink, ConstVK.BDAY);
        int month = getBDigit(bdayAndMonthLink, ConstVK.BMONTH);
        String result = "-00-00";
        if(month!=0 ){
            if(month<=9){
                result = "-0"+month;
            }else{
                result = "-"+String.valueOf(month);
            }
        }else{
            result = "-00";
        }
        if(day!=0 ){
            if(day<=9){
                result += "-0"+day;
            }else{
                result += "-"+String.valueOf(day);
            }
        }else{
            result += "-00";
        }
        return result;

    }

    private void testGetBDigit(){
        //Просто примеры вызовов, пущай хранятся
        String bdayAndMonthLink ="https://vk.com/search?c[section]=people&c[bday]=11&c[bmonth]=2";
        String byearLink = "/search?c[section]=people&c[byear]=1995";

        HelpFunctionalityVK hp = new HelpFunctionalityVK();
        int bday = hp.getBDigit(bdayAndMonthLink,ConstVK.BDAY);
        int bmonth = hp.getBDigit(bdayAndMonthLink,ConstVK.BMONTH);
        int byear = hp.getBDigit(byearLink,ConstVK.BYEAR);

        System.out.printf("Дата рождения: %d.%d.%d",bday,bmonth,byear);
    }

    //Метод для получения настоящего ID пользователя из строки со ссылкой на фото профиля
    public int getDefaultID(String input){
        int id = 0;

        //Стандартный вид входящей строки
        //https://vk.com/feed?section=source&source=226361909
        //https://vk.com/photo176464710_456239756 //по фотографии профиля, но ее может и не быть

        Pattern pattern = Pattern.compile("source&source=");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            input = input.substring(matcher.end(),input.length());
            try{
                id = Integer.parseInt(input);
                System.out.println("Получили число: '"+id+"'");
            }catch (NumberFormatException e){
                System.out.println("Ошибка в преобразовании строки "+input+" в int");
            }
        }else{
            System.out.println("Нет совпадений для "+input+" при поиске ID");
        }

        return id;
    }

    public int getDefaultIDByWall(String input) throws SearchIDException {
        //https://vk.com/wall437245261?own=1
        //https://vk.com/wall437245261
        Pattern p1 = Pattern.compile("wall");
        Matcher m1 = p1.matcher(input);

        Pattern p2 = Pattern.compile("\\?");
        Matcher m2 = p2.matcher(input);

        if (m1.find() & m2.find()) {
            input = input.substring(m1.end(),m2.start());
            //System.out.println("Выковыряли: "+input);
            try{
                int id = Integer.parseInt(input);
                //System.out.println("Получили число: '"+id+"'");
                return id;
            }catch (NumberFormatException e){
                System.out.println("Ошибка в преобразовании строки "+input+" в int");
                throw new SearchIDException("Ошибка в преобразовании типов");
            }
        }else{
            Pattern p3 = Pattern.compile("wall");
            Matcher m3 = p3.matcher(input);
            if(m3.find()){
                input = input.substring(m3.end(),input.length());
                //System.out.println("Выковыряли: "+input);
                try{
                    int id = Integer.parseInt(input);
                    //System.out.println("Получили число: '"+id+"'");
                    return id;
                }catch (NumberFormatException e){
                    System.out.println("Ошибка в преобразовании строки "+input+" в int");
                    throw new SearchIDException("Ошибка в преобразовании типов");
                }
            }else{
                throw new SearchIDException("Нет совпадений в строке поиска");
            }
        }

    }
}
