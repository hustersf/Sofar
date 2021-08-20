package com.sofar.preloader.compiler;

import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.sofar.preloader.annotation.PreLoad;

@SupportedAnnotationTypes({"com.sofar.preloader.annotation.PreLoad"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class PreLoadProcessor extends BaseProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    note("PreLoadProcessor start\n");
    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(PreLoad.class);
    for (Element element : elements) {
      note(element.toString());
      ElementKind kind = element.getKind();
      if (kind == ElementKind.CLASS) {

      } else if (kind == ElementKind.METHOD) {

      } else {
        error("not support kind=" + kind.toString());
      }
    }
    return false;
  }

}
