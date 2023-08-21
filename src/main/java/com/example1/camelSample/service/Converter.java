package com.example1.camelSample.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example1.camelSample.entity.ConvRule;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Converter {
    @Value("${my.camel.tmpdir}")
    public String tmpdirpath;

    public File convert(File f, ConvRule c) {

        List<String> lines = new ArrayList<String>();

        String lineCd = "\n";
        if (!lineCd.equals(c.getDstLinefeed())) {
            lineCd = "\r\n";
        }
        Path tmpFile=Paths.get(tmpdirpath);
        try {
            tmpFile = Files.createTempFile(Paths.get(tmpdirpath), f.getName() + ".", ".tmp");
            lines = Files.readAllLines(f.toPath(), Charset.forName(c.getSrcCharset()));
            log.info("srcCharset="+c.getDstCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        
        try (BufferedWriter out = Files.newBufferedWriter(tmpFile, Charset.forName(c.getDstCharset()));){
            for (int i = 0; i < lines.size() - c.getFooterdel(); i++) {
                 out.write(lines.get(i) + lineCd);
            }
            return tmpFile.toFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
