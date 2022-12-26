package com.spring.springbatch.listener;

import com.spring.springbatch.model.StudentCsv;
import com.spring.springbatch.model.StudentJson;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

@Component
public class SkipListener {

    @OnSkipInRead
    public void skipInRead(Exception ex){
        if (ex instanceof FlatFileParseException){
            createFile("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\batch-bad-records\\reader\\reader-bads.txt",
                    ((FlatFileParseException) ex).getInput());
        }
    }

    @OnSkipInProcess
    public void skipInProcess(StudentCsv studentCsv, Exception e){
        System.out.println(studentCsv.toString());

        createFile("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\batch-bad-records\\processor\\process-bads.txt",
                e.getLocalizedMessage());

    }

    @OnSkipInWrite
    public void skipInWrite(StudentJson studentJson,Exception e){
        createFile("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\batch-bad-records\\writer\\writer-bads.txt",
                studentJson.toString());
    }

    public void createFile(String path, String data){
        try(FileWriter fileWriter = new FileWriter(new File(path), true)) {
            fileWriter.write(data + "," + new Date() + "\n");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
