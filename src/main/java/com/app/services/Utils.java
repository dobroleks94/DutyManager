package com.app.services;

import com.app.controller.CourseInfoController;
import com.app.entity.Faculty;
import com.app.entity.FacultyDutyInfo;
import com.app.entity.FacultyUnit;
import com.app.entity.JsonGenEntity;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codehaus.jackson.map.ObjectMapper;
import pl.jsolve.templ4docx.core.Docx;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static String monthValueConverter(int month){
        return (month == 1) ? "січня" :
                (month == 2) ? "лютого" :
                        (month == 3) ? "березня" :
                                (month == 4) ? "квітня" :
                                        (month == 5) ? "травня" :
                                                (month == 6) ? "червня" :
                                                        (month == 7) ? "липня" :
                                                                (month == 8) ? "серпня" :
                                                                        (month == 9) ? "вересня" :
                                                                                (month == 10) ? "жовтня" :
                                                                                        (month == 11) ? "листопада"
                                                                                                : "грудня";
    }


    public static List<String> getNamesFrom(String field) {
        String splitterPattern = "[,;]\\s*";
        return Arrays.stream(field.split(splitterPattern)).filter(i -> !"".equals(i)).collect(Collectors.toList());
    }
    public static Map<String, Integer> getOtherCadetAbsenceInfo(List<String> namesDigits){
        String splitterPattern = "\\s*-\\s*";
        Map<String, Integer> values = new LinkedHashMap<>();
        namesDigits.forEach(item -> values.put(item.split(splitterPattern)[0], Integer.parseInt(item.split(splitterPattern)[1])));
        return values;
    }

    public static boolean tryParse(String str){
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public static String trimList(List<String> list) {
        return list.toString().substring(1, list.toString().length() - 1);
    }

    public static String trimMap(Map<String, Integer> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((descr, count) -> sb.append(descr).append(" - ").append(count).append("; "));
        return sb.toString().trim();
    }

    public static String buildAdditionalDataString(List<FacultyUnit> unit, String additionalData){
        StringBuilder sb = new StringBuilder(additionalData + ": ");

        for(FacultyUnit single : unit){
            String cadets = "";
            switch (additionalData){
                case "відп":
                    cadets = Utils.trimList(single.getVacationsCadets());
                    break;
                case "зв-ня":
                    cadets = Utils.trimList(single.getOnLeaveCadets());
                    break;
                case "відр":
                    cadets = Utils.trimList(single.getDetachedCadets());
                    break;
            }
            if ("".equals(cadets))
                continue;
            sb.append(String.format(" %dк - %s; ", single.getUnitNumber(), cadets));
        }

        if("відп: ".equals(sb.toString()) || "зв-ня: ".equals(sb.toString()) || "відр: ".equals(sb.toString()))
            return "";

        return sb.toString().trim();
    }

    public static void saveFile(Docx template, Stage mainPageStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти розхід ...");
        File newDutyInfoList = fileChooser.showSaveDialog(mainPageStage);
        template.save(newDutyInfoList.getPath());
    }

    public static void saveJsonRepresentation(List<FacultyUnit> units, FacultyDutyInfo info){
        JsonGenEntity entityToSave = new JsonGenEntity(info, units);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new FileOutputStream(new File("./previousDutyList.json")), entityToSave);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ooooops :((((((((((((((");
        }
    }

    public static boolean getEntitiesFromJsonRepresentation(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonGenEntity entity ;
        try {
            InputStream inputStream = new FileInputStream("./previousDutyList.json");
            entity = objectMapper.readValue(inputStream, JsonGenEntity.class);
            FacultyDutyInfo info = entity.getDutyInformation();
            List<FacultyUnit> units = entity.getFacultyUnits();
            info.setFaculty(Faculty.getByNumber(info.getFacultyNum()));

            FacultyDataService.updateFacultyDutyInfo(info);
            CourseInfoController.setFacultyUnits(units);

            System.out.println(entity);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            StageCreationService.showFileNotExist();
        }
        return false;
    }

}
