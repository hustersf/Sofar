package com.sofar.preloader.compiler;

import java.io.File;
import java.io.FileWriter;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public abstract class BaseProcessor extends AbstractProcessor {

  protected Filer filer;
  protected Elements elements;
  protected Types types;
  protected Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    filer = processingEnv.getFiler();
    elements = processingEnv.getElementUtils();
    types = processingEnv.getTypeUtils();
    messager = processingEnv.getMessager();
  }

  protected void warn(String msg) {
    messager.printMessage(Diagnostic.Kind.WARNING, msg);
  }

  protected void note(String msg) {
    messager.printMessage(Diagnostic.Kind.NOTE, msg);
  }

  protected void error(String msg) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg);
  }

  protected void testWriteFile(String filePath, String text) {
    File file = new File(filePath);
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter writer = new FileWriter(file, true);
      writer.write(text);
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
