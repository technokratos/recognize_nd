module org.dbk.recognize_gravitation {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires jdk.internal.vm.compiler;
    requires kotlin.test;


    opens org.dbk.recognize_gravitation to javafx.fxml;
    exports org.dbk.recognize_gravitation;
}