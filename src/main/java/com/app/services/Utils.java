package com.app.services;

import com.app.controller.CourseInfoController;
import com.app.entity.Faculty;
import com.app.entity.FacultyDutyInfo;
import com.app.entity.FacultyUnit;
import com.app.entity.JsonGenEntity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import org.codehaus.jackson.map.ObjectMapper;
import pl.jsolve.templ4docx.core.Docx;

public class Utils {

    public Utils() { }

    public static String monthValueConverter(int month) {
        return month == 1 ? "січня" :
                month == 2 ? "лютого" :
                        month == 3 ? "березня" :
                                month == 4 ? "квітня" :
                                        month == 5 ? "травня" :
                                                month == 6 ? "червня" :
                                                        month == 7 ? "липня" :
                                                                month == 8 ? "серпня" :
                                                                        month == 9 ? "вересня" :
                                                                                month == 10 ? "жовтня" :
                                                                                        month == 11 ? "листопада" : "грудня";
    }

    public static List<String> getNamesFrom(String field) {
        String splitterPattern = "[,;]\\s*";
        return Arrays.stream(field.split(splitterPattern)).filter((i) -> !"".equals(i)).collect(Collectors.toList());
    }

    public static Map<String, Integer> getOtherCadetAbsenceInfo(List<String> namesDigits) {
        String splitterPattern = "\\s*-\\s*";
        Map<String, Integer> values = new LinkedHashMap();
        namesDigits.forEach((item) -> values.put(item.split(splitterPattern)[0], Integer.parseInt(item.split(splitterPattern)[1])));
        return values;
    }

    public static boolean tryParse(String str) {
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static String trimList(List<String> list) {
        if (!list.isEmpty())
            return list.toString().substring(1, list.toString().length() - 1);
        return "";
    }

    public static String trimMap(Map<String, ?> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((descr, count) -> {
            sb.append(descr).append(" - ").append(count).append("; ");
        });
        return sb.toString().trim();
    }

    public static String buildAdditionalDataString(List<FacultyUnit> unit, String additionalData, int womenAdj) {
        StringBuilder sb = new StringBuilder(additionalData + ": ");

        for(FacultyUnit single : unit){
            String cadets = "";
            String women = "";
            switch (additionalData){
                case "відп":
                    cadets = Utils.trimList(single.getVacationsCadets());
                    women = Utils.trimList(single.getWomen().getVacationsCadets());
                    break;
                case "зв-ня":
                    cadets = Utils.trimList(single.getOnLeaveCadets());
                    women = Utils.trimList(single.getWomen().getOnLeaveCadets());
                    break;
                case "відр":
                    cadets = Utils.trimList(single.getDetachedCadets());
                    women = Utils.trimList(single.getWomen().getDetachedCadets());
                    break;
            }
            if ("".equals(cadets) & "".equals(women) || ("".equals(cadets) & womenAdj == 2))
                continue;
            switch (womenAdj){
                case 1:
                    if(!"".equals(women)) sb.append(String.format(" %d - %s (%s); ", single.getUnitNumber(), cadets, women));
                    else sb.append(String.format(" %dк - %s; ", single.getUnitNumber(), cadets));
                    break;
                case 2:
                    sb.append(String.format(" %dк - %s; ", single.getUnitNumber(), cadets));
                    break;
                default:
                    String surnames = !"".equals(cadets)
                            ? (!"".equals(women) ? cadets.concat(", " + women) : cadets)
                            : women;
                    sb.append(String.format( "%d - %s; ", single.getUnitNumber(), surnames));
            }
        }

        if("відп: ".equals(sb.toString()) || "зв-ня: ".equals(sb.toString()) || "відр: ".equals(sb.toString()))
            return "";

        return sb.toString().trim();
    }

    public static Path saveFile(Docx template, Stage mainPageStage, FacultyDutyInfo dutyInfo) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти розхід ...");
        String timeRepres = dutyInfo.getDutyTime().replaceAll(":", "-");
        fileChooser.setInitialFileName(timeRepres + ".docx");
        ExtensionFilter wordExt = new ExtensionFilter("Microsoft Word Document (.docx, .doc)", new String[]{"*.docx", "*.doc"});
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter[]{wordExt});
        File newDutyInfoList = fileChooser.showSaveDialog(mainPageStage);
        template.save(newDutyInfoList.getPath());

        return Paths.get(newDutyInfoList.toURI());
    }

    public static void saveJsonRepresentation(List<FacultyUnit> units, FacultyDutyInfo info) {
        JsonGenEntity entityToSave = new JsonGenEntity(info, units);
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new FileOutputStream(new File("./previousDutyList.json")), entityToSave);
        } catch (IOException var5) {
            var5.printStackTrace();
            System.out.println("Ooooops :((((((((((((((");
        }

    }

    public static boolean getEntitiesFromJsonRepresentation() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = new FileInputStream("./previousDutyList.json");
            JsonGenEntity entity = objectMapper.readValue(inputStream, JsonGenEntity.class);
            FacultyDutyInfo info = entity.getDutyInformation();
            List<FacultyUnit> units = entity.getFacultyUnits();
            info.setFaculty(Faculty.getByNumber(info.getFacultyNum()));
            FacultyDataService.updateFacultyDutyInfo(info);
            CourseInfoController.setFacultyUnits(units);
            System.out.println(entity);
            return true;
        } catch (IOException var5) {
            var5.printStackTrace();
            StageCreationService.showFileNotExist();
            return false;
        }
    }
}
