/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 *
 * @author Rollie
 */

public class rayTraceRender extends Application {
    static ImageView imageView = new ImageView();
    boolean isFloorOn = true;
    boolean antialiasing = false;
    int antialiasType = 1;//init supersampling
    int objectIterator = 0;
    int lightIterator = 0;
    int materialIterator = 0;
    int antialiasingDepth = 2;
    int previewAccuracy = 4;
    double antialiasFilterWidth = 1.0;
    double tX = 0, tY = 1, tZ = 0, pX = 5, pY = 5, pZ = 5;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    
    View view;
    View materialView;
    View lightView;
    Floor floor;
    Sphere rmFloor;
    
    ListView<String> objectList = new ListView<>();
    ListView<String> lightList = new ListView<>();
    ListView<String> materialList = new ListView<>();
        
    ObservableList<String> objectListItems = FXCollections.observableArrayList();
    ObservableList<String> lightListItems = FXCollections.observableArrayList();
    ObservableList<String> materialListItems = FXCollections.observableArrayList();
    
    public rayTraceRender() {
        this.floor = new Floor() {{
            material = Materials.Floor;
            objectPosition = Vector.create(0, 1, 0);
            displacement = 0;
            name = "Floor";
        }};

        this.rmFloor = new Sphere() {{
            material = Materials.Floor;
            objectPosition = Vector.create(0, -10000, 0);
            radius = 0.0000001;
            name = "Floor";
        }};
        
        this.view = new View()
        {{
            objects = new AbsObject[] {
                floor,
                new Sphere() {{
                    material = Materials.Glass;
                    objectPosition = Vector.create(0, 3, 1);
                    radius = 1.0;
                    name = "Sphere." + objectIterator;
                    objectIterator++;
                }},
                new Sphere() {{
                    material = Materials.Glass;
                    objectPosition = Vector.create(4, 3, 1);
                    radius = 1.5;
                    name = "Sphere." + objectIterator;
                    objectIterator++;
                }},
                new Sphere() {{
                    material = Materials.Mirror;
                    objectPosition = Vector.create(0, 1, 0);
                    radius = 0.5;
                    name = "Sphere." + objectIterator;
                    objectIterator++;
                }},
                new Sphere() {{
                    material = Materials.Mirror;
                    objectPosition = Vector.create(-4, 5, 2);
                    radius = 2.0;
                    name = "Sphere." + objectIterator;
                    objectIterator++;
                }},
                new Sphere() {{
                    material = Materials.Mirror;
                    objectPosition = Vector.create(3, 2, 6);
                    radius = 1.75;
                    name = "Sphere." + objectIterator;
                    objectIterator++;
                }},
                new Sphere() {{
                    material = Materials.Mirror;
                    objectPosition = Vector.create(-5, 5, -6);
                    radius = 4.0;
                    name = "Sphere." + objectIterator;
                    objectIterator++;
                }}
            };
            lightSource = new LightSource[] {
                new LightSource() {{
                    color = Colour.create(0.5, 0.3, 0.75);
                    position = Vector.create(-2, 5, 0);
                    name = "Light." + lightIterator;
                    lightIterator++;
                }},
                new LightSource() {{
                    color = Colour.create(0.5, 0.5, 0.5);
                    position = Vector.create(-10, 12, -7.5);
                    name = "Light." + lightIterator;
                    lightIterator++;
                }},
                new LightSource() {{
                    color = Colour.create(0.5, 0.6, 0.3);
                    position = Vector.create(-6, 8, 5);
                    name = "Light." + lightIterator;
                    lightIterator++;
                }},
                new LightSource() {{
                    color = Colour.create(0.5, 0.1, 0.1);
                    position = Vector.create(4, 2.5, -5);
                    name = "Light." + lightIterator;
                    lightIterator++;
                }}};
            camera = Camera.create(Vector.create(0, 1, 0), Vector.create(5, 5, 5));
        }};
        
        this.materialView = new View(){{
            objects = new AbsObject[]{
                new Sphere() {{
                    material = Materials.Mirror;
                    objectPosition = Vector.create(0, 1, 0);
                    radius = 1.0;
                    name = "Sphere";
                }},
                floor
            };
            lightSource = new LightSource[] {
                new LightSource() {{
                    color = Colour.create(0.75, 0.75, 0.75);
                    position = Vector.create(4, 3, 4);
                    name = "Light";
                }},
                new LightSource() {{
                    color = Colour.create(0.75, 0.75, 0.75);
                    position = Vector.create(4, 3, -4);
                    name = "Light";
                }},
                new LightSource() {{
                    color = Colour.create(0.75, 0.75, 0.75);
                    position = Vector.create(-4, 3, 4);
                    name = "Light";
                }},
                new LightSource() {{
                    color = Colour.create(0.75, 0.75, 0.75);
                    position = Vector.create(-4, 3, -4);
                    name = "Light";
                }}
            };
            camera = Camera.create(Vector.create(0, 1, 0), Vector.create(3, 3, 3), 600, 450);
        }};

        this.lightView = new View(){{
            objects = new AbsObject[]{
                floor
            };
            lightSource = new LightSource[] {
                new LightSource() {{
                    color = Colour.create(1.0, 1.0, 1.0);
                    position = Vector.create(0, 1, 0);
                    name = "Light";
                }}
            };
            camera = Camera.create(Vector.create(0, 1, 0), Vector.create(3, 3, 3), 600, 450);
        }};
    }
    
    @Override
    public void start(Stage primaryStage) {
        alert.setTitle("Rendering previews...");
        alert.setHeaderText("Rendering previews for main window, add material window,\nand add light window. Please wait...");
        alert.initModality(Modality.NONE);
        alert.show();
        
        Materials.materials = new Material[] {
            Materials.Default,
            Materials.Floor,
            Materials.Mirror,
            Materials.Glass
        };

        ImageView imageViewLP = new ImageView();
        ImageView imageViewMP = new ImageView();

        Label colorLP = new Label("Color");
        Label positionLP = new Label("Position");
        Label nameLLP = new Label("Name");
        Label emptyLP = new Label("");
        Label emptyLP2 = new Label("");
        Label specularMP = new Label("Specularity");
        Label diffusionMP = new Label("Diffusion");
        Label reflectionMP = new Label("Reflection");
        Label accuracyMP = new Label("Specular Width");
        Label nameLMP = new Label("Name");
        Label emptyMP = new Label("");
        Label emptyMP2 = new Label("");
        Label materialOP = new Label("Material");
        Label positionOP = new Label("Position");
        Label sizeOP = new Label("Size");
        Label nameLOP = new Label("Name");
        Label objectLOP = new Label("Object");
        Label empty = new Label("");
        Label targetCP = new Label("Target");
        Label positionCP = new Label("Position");
        Label rendFlo = new Label("Render Floor");
        Label anti = new Label("Antialiasing");
        Label previewQ = new Label("Preview Quality");
        Label antiType = new Label("Antialiasing Type");
        Label antiDepth = new Label("Antialiasing Depth");
        Label antiWidth = new Label("Antialiasing Width");
        Label emptyLeftL = new Label("");
        Label objectLabel = new Label("Objects");
        Label lightLabel = new Label("Lights");
        Label materialLabel = new Label("Materials");
        
        Button removeLP = new Button("Delete");
        Button saveLP = new Button("Save");
        Button renderLP = new Button("Render");
        Button removeMP = new Button("Delete");
        Button saveMP = new Button("Save");
        Button renderMP = new Button("Render");
        Button removeOP = new Button("Delete");
        Button saveOP = new Button("Save");
        Button cancelCP = new Button("Cancel");
        Button saveCP = new Button("Save");
        Button cancelS = new Button("Cancel");
        Button saveS = new Button("Save");
        Button render = new Button("Render");
        Button preview = new Button("Preview");
        Button addMaterial = new Button("Add Material");
        Button addObject = new Button("Add Object");
        Button addLight = new Button("Add Light");
        Button editCamera = new Button("Edit Camera");
        Button settings = new Button("Settings");
        Button deleteObject = new Button("Delete Object");
        Button deleteLight = new Button("Delete Light");
        Button deleteMaterial = new Button("Delete Material");

        ComboBox<String> cmbOP = new ComboBox<>();
        ComboBox<String> objectsOP = new ComboBox<>();
        ComboBox<String> antialiasingType = new ComboBox<>();
        ComboBox<String> antialiasingLevel = new ComboBox<>();
        ComboBox<String> previewQuality = new ComboBox<>();

        CheckBox renderFloor = new CheckBox();
        CheckBox antialias = new CheckBox();

        HBox colorHLP = new HBox(5);
        HBox positionHLP = new HBox(5);
        HBox butLP = new HBox(5);
        HBox specularHMP = new HBox(5);
        HBox diffusionHMP = new HBox(5);
        HBox butMP = new HBox(5);
        HBox posOP = new HBox(5);
        HBox butOP = new HBox(5);
        HBox tarCP = new HBox(5);
        HBox posCP = new HBox(5);
        HBox butCP = new HBox(5);
        HBox butS = new HBox(5);
        HBox hBox = new HBox(5);

        HBox.setMargin(hBox, new Insets(10));
        
        VBox vBox = new VBox(5);

        TextField nameLP = new TextField();
        TextField rLP = new TextField();
        TextField gLP = new TextField();
        TextField bLP = new TextField();
        TextField xLP = new TextField();
        TextField yLP = new TextField();
        TextField zLP = new TextField();
        TextField nameMP = new TextField();
        TextField specularRMP = new TextField();
        TextField specularGMP = new TextField();
        TextField specularBMP = new TextField();
        TextField diffusionRMP = new TextField();
        TextField diffusionGMP = new TextField();
        TextField diffusionBMP = new TextField();
        TextField reflectMP = new TextField();
        TextField accurMP = new TextField();
        TextField nameOP = new TextField();
        TextField xOP = new TextField();
        TextField yOP = new TextField();
        TextField zOP = new TextField();
        TextField radiusOP = new TextField();
        TextField xCP = new TextField();
        TextField yCP = new TextField();
        TextField zCP = new TextField();
        TextField xCPT = new TextField();
        TextField yCPT = new TextField();
        TextField zCPT = new TextField();
        TextField antialiasWidth = new TextField();

        GridPane addLightGP = new GridPane();
        GridPane addMaterialGP = new GridPane();
        GridPane addObjectGP = new GridPane();
        GridPane editCameraGP = new GridPane();
        GridPane settingsGP = new GridPane();
        GridPane root = new GridPane();

        GridPane.setConstraints(imageViewLP, 1, 0);
        GridPane.setConstraints(emptyLP2, 1, 1);
        GridPane.setConstraints(colorLP, 0, 2);
        GridPane.setConstraints(positionLP, 0, 3);
        GridPane.setConstraints(nameLLP, 0, 4);
        GridPane.setConstraints(colorHLP, 1, 2);
        GridPane.setConstraints(positionHLP, 1, 3);
        GridPane.setConstraints(nameLP, 1, 4);
        GridPane.setConstraints(emptyLP, 1, 5);
        GridPane.setConstraints(butLP, 1, 6);
        GridPane.setConstraints(imageViewMP, 1, 0);
        GridPane.setConstraints(emptyMP2, 1, 1);
        GridPane.setConstraints(specularMP, 0, 2);
        GridPane.setConstraints(diffusionMP, 0, 3);
        GridPane.setConstraints(reflectionMP, 0, 4);
        GridPane.setConstraints(specularHMP, 1, 2);
        GridPane.setConstraints(diffusionHMP, 1, 3);
        GridPane.setConstraints(reflectMP, 1, 4);
        GridPane.setConstraints(accuracyMP, 0, 5);
        GridPane.setConstraints(accurMP, 1, 5);
        GridPane.setConstraints(nameLMP, 0, 6);
        GridPane.setConstraints(nameMP, 1, 6);
        GridPane.setConstraints(emptyMP, 1, 7);
        GridPane.setConstraints(butMP, 1, 8);
        GridPane.setConstraints(materialOP, 0, 0);
        GridPane.setConstraints(cmbOP, 1, 0);
        GridPane.setConstraints(objectsOP, 1, 1);
        GridPane.setConstraints(objectLOP, 0, 1);
        GridPane.setConstraints(positionOP, 0, 2);
        GridPane.setConstraints(sizeOP, 0, 3);
        GridPane.setConstraints(posOP, 1, 2);
        GridPane.setConstraints(radiusOP, 1, 3);
        GridPane.setConstraints(nameLOP, 0, 4);
        GridPane.setConstraints(nameOP, 1, 4);
        GridPane.setConstraints(empty, 1, 5);
        GridPane.setConstraints(butOP, 1, 6);
        GridPane.setConstraints(targetCP, 0, 0);
        GridPane.setConstraints(tarCP, 1, 0);
        GridPane.setConstraints(positionCP, 0, 1);
        GridPane.setConstraints(posCP, 1, 1);
        GridPane.setConstraints(butCP, 1, 2);
        GridPane.setConstraints(rendFlo, 0, 0);
        GridPane.setConstraints(renderFloor, 1, 0);
        GridPane.setConstraints(anti, 0, 1);
        GridPane.setConstraints(antialias, 1, 1);
        GridPane.setConstraints(previewQ, 0, 2);
        GridPane.setConstraints(previewQuality, 1, 2);
        GridPane.setConstraints(antiWidth, 0, 3);
        GridPane.setConstraints(antialiasWidth, 1, 3);
        GridPane.setConstraints(antiType, 0, 4);
        GridPane.setConstraints(antialiasingType, 1, 4);
        GridPane.setConstraints(antiDepth, 0, 5);
        GridPane.setConstraints(antialiasingLevel, 1, 5);
        GridPane.setConstraints(butS, 1, 6);
        GridPane.setConstraints(imageView, 0, 0);
        GridPane.setConstraints(hBox, 0, 1);
        GridPane.setConstraints(vBox, 1, 0);

        addLightGP.setPadding(new Insets(10));
        colorLP.setPadding(new Insets(10));
        positionLP.setPadding(new Insets(10));
        nameLLP.setPadding(new Insets(10));
        nameLP.setPadding(new Insets(10));
        colorHLP.setPadding(new Insets(5, 0, 0, 0));
        positionHLP.setPadding(new Insets(5, 0, 0, 0));
        removeLP.setPadding(new Insets(10, 82, 10, 82));
        saveLP.setPadding(new Insets(10, 82, 10, 82));
        renderLP.setPadding(new Insets(10, 82, 10, 82));
        addMaterialGP.setPadding(new Insets(10));
        specularMP.setPadding(new Insets(10));
        diffusionMP.setPadding(new Insets(10));
        reflectionMP.setPadding(new Insets(10));
        accuracyMP.setPadding(new Insets(10));
        nameLMP.setPadding(new Insets(10));
        specularHMP.setPadding(new Insets(5, 0, 0, 0));
        diffusionHMP.setPadding(new Insets(5, 0, 0, 0));
        removeMP.setPadding(new Insets(10, 82, 10, 82));
        saveMP.setPadding(new Insets(10, 82, 10, 82));
        renderMP.setPadding(new Insets(10, 82, 10, 82));
        addObjectGP.setPadding(new Insets(10));
        materialOP.setPadding(new Insets(10));
        positionOP.setPadding(new Insets(10));
        sizeOP.setPadding(new Insets(10));
        nameLOP.setPadding(new Insets(10));
        objectLOP.setPadding(new Insets(10));
        posOP.setPadding(new Insets(5, 0, 0, 0));
        removeOP.setPadding(new Insets(10, 98, 10, 98));
        saveOP.setPadding(new Insets(10, 98, 10, 98));
        editCameraGP.setPadding(new Insets(10));
        targetCP.setPadding(new Insets(10));
        positionCP.setPadding(new Insets(10));
        tarCP.setPadding(new Insets(5, 0, 0, 0));
        posCP.setPadding(new Insets(5, 0, 0, 0));
        cancelCP.setPadding(new Insets(10, 98, 10, 98));
        saveCP.setPadding(new Insets(10, 98, 10, 98));
        settingsGP.setPadding(new Insets(10));
        rendFlo.setPadding(new Insets(10));
        anti.setPadding(new Insets(10));
        previewQ.setPadding(new Insets(10));
        antiWidth.setPadding(new Insets(10));
        antiType.setPadding(new Insets(10));
        antiDepth.setPadding(new Insets(10));
        cancelS.setPadding(new Insets(10, 30, 10, 30));
        saveS.setPadding(new Insets(10, 30, 10, 30));
        render.setPadding(new Insets(10, 25, 10, 25));
        preview.setPadding(new Insets(10, 25, 10, 25));
        addMaterial.setPadding(new Insets(10, 25, 10, 25));
        addObject.setPadding(new Insets(10, 25, 10, 25));
        addLight.setPadding(new Insets(10, 25, 10, 25));
        editCamera.setPadding(new Insets(10, 25, 10, 25));
        settings.setPadding(new Insets(10, 25, 10, 25));
        hBox.setPadding(new Insets(5));
        emptyLeftL.setPadding(new Insets(-10, 0, -10, 0));
        objectLabel.setPadding(new Insets(5, 0, 0, 7));
        lightLabel.setPadding(new Insets(5, 0, 0, 7));
        materialLabel.setPadding(new Insets(5, 0, 0, 7));
        deleteObject.setPadding(new Insets(10));
        deleteLight.setPadding(new Insets(10));
        deleteMaterial.setPadding(new Insets(10));
        vBox.setPadding(new Insets(0, 0, 0, 5));

        nameLP.setPromptText("Name");
        rLP.setPromptText("R (0 -> 1)");
        gLP.setPromptText("G (0 -> 1)");
        bLP.setPromptText("B (0 -> 1)");
        xLP.setPromptText("X (R)");
        yLP.setPromptText("Y (R)");
        zLP.setPromptText("Z (R)");
        nameMP.setPromptText("Name");
        specularRMP.setPromptText("R (0 -> 1)");
        specularGMP.setPromptText("G (0 -> 1)");
        specularBMP.setPromptText("B (0 -> 1)");
        diffusionRMP.setPromptText("R (0 -> 1)");
        diffusionGMP.setPromptText("G (0 -> 1)");
        diffusionBMP.setPromptText("B (0 -> 1)");
        reflectMP.setPromptText("Reflection (0 -> 1)");
        accurMP.setPromptText("Accuracy (0 -> N)");
        nameOP.setPromptText("Name");
        xOP.setPromptText("X");
        yOP.setPromptText("Y");
        zOP.setPromptText("Z");
        radiusOP.setPromptText("Radius (Sphere Only)");
        xCP.setPromptText("X");
        yCP.setPromptText("Y");
        zCP.setPromptText("Z");
        xCPT.setPromptText("X");
        yCPT.setPromptText("Y");
        zCPT.setPromptText("Z");
        antialiasWidth.setPromptText("Cubic Filter (0.0 -> R)");

        Scene lightScene = new Scene(addLightGP);
        Scene materialScene = new Scene(addMaterialGP);
        Scene objectScene = new Scene(addObjectGP);
        Scene cameraScene = new Scene(editCameraGP);
        Scene settingsScene = new Scene(settingsGP);
        Scene scene = new Scene(root, 1010, 850);

        Stage addLightPopup = new Stage();
        Stage addMaterialPopup = new Stage();
        Stage addObjectPopup = new Stage();
        Stage editCameraPopup = new Stage();
        Stage settingsPopup = new Stage();
        
        addLightPopup.setAlwaysOnTop(true);
        addMaterialPopup.setAlwaysOnTop(true);
        addObjectPopup.setAlwaysOnTop(true);
        editCameraPopup.setAlwaysOnTop(true);
        settingsPopup.setAlwaysOnTop(true);

        addLightPopup.initModality(Modality.WINDOW_MODAL);
        addMaterialPopup.initModality(Modality.WINDOW_MODAL);
        addObjectPopup.initModality(Modality.WINDOW_MODAL);
        editCameraPopup.initModality(Modality.WINDOW_MODAL);
        settingsPopup.initModality(Modality.WINDOW_MODAL);

        colorHLP.getChildren().addAll(rLP, gLP, bLP);
        positionHLP.getChildren().addAll(xLP, yLP, zLP);
        butLP.getChildren().addAll(removeLP, saveLP, renderLP);
        addLightGP.getChildren().addAll(imageViewLP, emptyLP2, colorLP, positionLP, nameLLP, colorHLP, positionHLP, nameLP, emptyLP, butLP);
        specularHMP.getChildren().addAll(specularRMP, specularGMP, specularBMP);
        diffusionHMP.getChildren().addAll(diffusionRMP, diffusionGMP, diffusionBMP);
        butMP.getChildren().addAll(removeMP, saveMP, renderMP);
        addMaterialGP.getChildren().addAll(imageViewMP, specularMP, diffusionMP, specularHMP, diffusionHMP, reflectionMP, butMP, emptyMP, reflectMP, nameLMP, nameMP, accuracyMP, accurMP);
        posOP.getChildren().addAll(xOP, yOP, zOP);
        butOP.getChildren().addAll(removeOP, saveOP);
        addObjectGP.getChildren().addAll(materialOP, objectsOP, objectLOP, positionOP, sizeOP, butOP, empty, cmbOP, posOP, radiusOP, nameLOP, nameOP);
        tarCP.getChildren().addAll(xCPT, yCPT, zCPT);
        posCP.getChildren().addAll(xCP, yCP, zCP);
        butCP.getChildren().addAll(cancelCP, saveCP);
        editCameraGP.getChildren().addAll(targetCP, tarCP, positionCP, posCP, butCP);
        butS.getChildren().addAll(cancelS, saveS);
        settingsGP.getChildren().addAll(rendFlo, anti, previewQ, previewQuality, renderFloor, antialias, antiType, antialiasingType, antiDepth, antialiasingLevel, butS, antiWidth, antialiasWidth);
        hBox.getChildren().addAll(render, preview, addMaterial, addObject, addLight, editCamera, settings);
        vBox.getChildren().addAll(emptyLeftL, objectLabel, deleteObject, objectList, lightLabel, deleteLight, lightList, materialLabel, deleteMaterial, materialList);
        root.getChildren().addAll(imageView, hBox, vBox);

        addLightPopup.setTitle("Add Light");
        addMaterialPopup.setTitle("Add Material");
        addObjectPopup.setTitle("Add Object");
        editCameraPopup.setTitle("Edit Camera");
        settingsPopup.setTitle("Settings");
        primaryStage.setTitle("Steven Rowland(rowland005) Ray Tracer");

        addLightPopup.setScene(lightScene);
        addMaterialPopup.setScene(materialScene);
        addObjectPopup.setScene(objectScene);
        editCameraPopup.setScene(cameraScene);
        settingsPopup.setScene(settingsScene);
        primaryStage.setScene(scene);
        
        xCP.setText(String.valueOf(pX));
        yCP.setText(String.valueOf(pY));
        zCP.setText(String.valueOf(pZ));
        
        xCPT.setText(String.valueOf(tX));
        yCPT.setText(String.valueOf(tY));
        zCPT.setText(String.valueOf(tZ));

        
        renderFloor.setSelected(true);
        antialias.setSelected(false);
        
        antialiasingType.getItems().add("Super Sampling");
        antialiasingType.getItems().add("Adaptive Sampling");
        antialiasingType.getItems().add("Stochastic Sampling");
        antialiasingType.setValue("Super Sampling");
        
        antialiasingLevel.getItems().add("2");
        antialiasingLevel.getItems().add("4");
        antialiasingLevel.getItems().add("8");
        antialiasingLevel.getItems().add("16");
        antialiasingLevel.getItems().add("32");
        antialiasingLevel.setValue("2");
        
        previewQuality.getItems().add("50%");
        previewQuality.getItems().add("25%");
        previewQuality.getItems().add("12.5%");
        previewQuality.getItems().add("6.25%");
        previewQuality.getItems().add("3.125%");
        previewQuality.setValue("25%");
        
        objectsOP.getItems().add("Sphere");
        objectsOP.setValue("Sphere");
        
        for (AbsObject i : view.objects){
            if(!"Floor".equals(i.name)){
                objectListItems.add(i.name);
            }
        }
        
        for (LightSource i : view.lightSource){
            lightListItems.add(i.name);
        }
        
        for (Material i : Materials.materials){
            materialListItems.add(i.name);
        }

        for(Material i : Materials.materials){
            cmbOP.getItems().add(i.name);
        }
        
        antialiasingType.setPrefWidth(185);
        antialiasingLevel.setPrefWidth(185);
        previewQuality.setPrefWidth(185);
        objectList.setItems(objectListItems);
        objectList.setPrefWidth(200);
        objectList.setPrefHeight(250);
        lightList.setItems(lightListItems);
        lightList.setPrefWidth(200);
        lightList.setPrefHeight(250);
        materialList.setItems(materialListItems);
        materialList.setPrefWidth(200);
        materialList.setPrefHeight(250);
        objectsOP.setPrefWidth(457);
        cmbOP.setPrefWidth(457);

        imageView.setImage(Render.renderPreview(800, 800, view, previewAccuracy));
        imageViewMP.setImage(Render.render(600, 450, materialView));
        imageViewLP.setImage(Render.render(600, 450, lightView));
        

        renderLP.setOnAction((ActionEvent event) -> {
            double r, g, b;
            
            try{
                r = Double.parseDouble(rLP.getText());
                g = Double.parseDouble(gLP.getText());
                b = Double.parseDouble(bLP.getText());
                
                LightSource lightTemp = new LightSource(){{
                    name = "Temp";
                    color = Colour.create(r, g, b);
                    position = Vector.create(0, 1, 0);
                }};
                
                lightView.lightSource[0] = lightTemp;
            }
            catch (Exception ex){
                
            }
            
            imageViewLP.setImage(Render.render(600, 450, lightView));
        });
        
        removeLP.setOnAction((ActionEvent event) -> {
            addLightPopup.hide();
            rLP.setText("");
            gLP.setText("");
            bLP.setText("");

            xLP.setText("");
            yLP.setText("");
            zLP.setText("");

            nameLP.setText("");
        });
        
        saveLP.setOnAction((ActionEvent event) -> {
            String tempName;
            double r, g, b, x, y, z;
            
            try{
                if ("".equals(nameLP.getText())){
                    tempName = "Light." + lightIterator;
                    lightIterator++;
                }
                else{
                    tempName = nameLP.getText() + "." + lightIterator;
                    lightIterator++;
                }
                
                r = Double.parseDouble(rLP.getText());
                g = Double.parseDouble(gLP.getText());
                b = Double.parseDouble(bLP.getText());
                
                x = Double.parseDouble(xLP.getText());
                y = Double.parseDouble(yLP.getText());
                z = Double.parseDouble(zLP.getText());

                LightSource lightTemp = new LightSource(){{
                    name = "Temp";
                    color = Colour.create(r, g, b);
                    position = Vector.create(x, y, z);
                }};
                
                addLight(lightTemp, tempName);
                
                rLP.setText("");
                gLP.setText("");
                bLP.setText("");

                xLP.setText("");
                yLP.setText("");
                zLP.setText("");

                nameLP.setText("");
            }
            catch (Exception ex){
                
            }
            
            addLightPopup.hide();
        });
        
        renderMP.setOnAction((ActionEvent event) -> {
            double spr, spg, spb, dir, dig, dib, ref;
            int acc;
            
            try{
                spr = Double.parseDouble(specularRMP.getText());
                spg = Double.parseDouble(specularGMP.getText());
                spb = Double.parseDouble(specularBMP.getText());
                
                dir = Double.parseDouble(diffusionRMP.getText());
                dig = Double.parseDouble(diffusionGMP.getText());
                dib = Double.parseDouble(diffusionBMP.getText());
                
                ref = Double.parseDouble(reflectMP.getText());
                acc = Integer.parseInt(accurMP.getText());

                Material materialTemp = new Material(){{
                    name = "Temp";
                    reflect = (Vector position) -> ref;
                    diffuse = (Vector position) -> Colour.create(dir, dig, dib);
                    specular = (Vector position) -> Colour.create(spr, spg, spb);
                    specular = (Vector position) -> Colour.create(0.5, 0.5, 0.5);
                    specularWidth = 15;
                    specularWidth = acc;
                }};
                
                materialView.objects[0].material = materialTemp;
            }
            catch (Exception ex){
                
            }
            
            imageViewMP.setImage(Render.render(600, 450, materialView));
        });
        
        removeMP.setOnAction((ActionEvent event) -> {
            addMaterialPopup.hide();
            specularRMP.setText("");
            specularGMP.setText("");
            specularBMP.setText("");

            diffusionRMP.setText("");
            diffusionGMP.setText("");
            diffusionBMP.setText("");

            reflectMP.setText("");
            accurMP.setText("");
        });
        
        saveMP.setOnAction((ActionEvent event) -> {
            String tempName;
            double spr, spg, spb, dir, dig, dib, ref;
            int acc;
            
            try{
                if ("".equals(nameMP.getText())){
                    tempName = "Material." + materialIterator;
                    materialIterator++;
                }
                else{
                    tempName = nameMP.getText() + "." + materialIterator;
                    materialIterator++;
                }
                
                spr = Double.parseDouble(specularRMP.getText());
                spg = Double.parseDouble(specularGMP.getText());
                spb = Double.parseDouble(specularBMP.getText());
                
                dir = Double.parseDouble(diffusionRMP.getText());
                dig = Double.parseDouble(diffusionGMP.getText());
                dib = Double.parseDouble(diffusionBMP.getText());
                
                ref = Double.parseDouble(reflectMP.getText());
                acc = Integer.parseInt(accurMP.getText());

                Material materialTemp = new Material(){{
                    name = tempName;
                    reflect = (Vector position) -> ref;
                    diffuse = (Vector position) -> Colour.create(dir, dig, dib);
                    specular = (Vector position) -> Colour.create(spr, spg, spb);
                    specularWidth = acc;
                }};
                
                materialView.objects[0].material = materialTemp;
                
                addMaterial(materialTemp, tempName);
                
                specularRMP.setText("");
                specularGMP.setText("");
                specularBMP.setText("");

                diffusionRMP.setText("");
                diffusionGMP.setText("");
                diffusionBMP.setText("");

                reflectMP.setText("");
                accurMP.setText("");
            }
            catch (Exception ex){
                
            }
            
            addMaterialPopup.hide();
        });

        removeOP.setOnAction((ActionEvent event) -> {
            addObjectPopup.hide();
            xOP.setText("");
            yOP.setText("");
            zOP.setText("");
            radiusOP.setText("");
        });
        
        saveOP.setOnAction((ActionEvent event) -> {
            Material material = new Material();
            String name;
            
            String object = objectsOP.getValue();
            
            try{
                if(null != object)switch (object) {
                    case "Sphere":
                        //do nothing yet
                        break;
                    default:
                        //set sphere
                        break;
                }
                
                if ("".equals(cmbOP.getValue())){
                    material = Materials.Default;
                }
                else{
                    for(Material i : Materials.materials){
                        if (i.name.equals(cmbOP.getValue())){
                            material = i;
                        }
                    }
                }
                
                if ("".equals(nameOP.getText())){
                    name = "Object." + objectIterator;
                    objectIterator++;
                }
                else{
                    name = nameOP.getText() + "." + objectIterator;
                    objectIterator++;
                }
                
                name = name + "." + objectIterator;
                objectIterator++;
                
                addSphere(
                    name,
                    Double.parseDouble(xOP.getText()), 
                    Double.parseDouble(yOP.getText()), 
                    Double.parseDouble(zOP.getText()), 
                    Double.parseDouble(radiusOP.getText()),
                    material
                );
            }
            catch(Exception ex){
                
            }
            
            addObjectPopup.hide();
            xOP.setText("");
            yOP.setText("");
            zOP.setText("");
            radiusOP.setText("");
        });
        
        cancelCP.setOnAction((ActionEvent event) -> {
            editCameraPopup.hide();
        });
        
        saveCP.setOnAction((ActionEvent event) -> {
            try{
            
                tX = Double.parseDouble(xCPT.getText());
                tY = Double.parseDouble(yCPT.getText());
                tZ = Double.parseDouble(zCPT.getText());

                pX = Double.parseDouble(xCP.getText());
                pY = Double.parseDouble(yCP.getText());
                pZ = Double.parseDouble(zCP.getText());
                
                view.camera = Camera.create(Vector.create(tX, tY, tZ), Vector.create(pX, pY, pZ));
            }
            catch(Exception ex){}
            
            editCameraPopup.hide();
        });
        
        cancelS.setOnAction((ActionEvent event) -> {
            settingsPopup.hide();
        });
        
        saveS.setOnAction((ActionEvent event) -> {
            try{
                isFloorOn = renderFloor.isSelected();
                
                if (!isFloorOn){
                    view.objects[0] = rmFloor;
                }
                else{
                    view.objects[0] = floor;
                }
                
                antialiasing = antialias.isSelected();
                
                if (!"".equals(antialiasingLevel.getValue())){
                    antialiasingDepth = Integer.parseInt(antialiasingLevel.getValue());
                }
                
                String previewQual = previewQuality.getValue();
                
                if(null != previewQual)switch (previewQual) {
                    case "50%":
                        previewAccuracy = 2;
                        break;
                    case "25%":
                        previewAccuracy = 4;
                        break;
                    case "12.5%":
                        previewAccuracy = 8;
                        break;
                    case "6.25%":
                        previewAccuracy = 16;
                        break;
                    case "3.125%":
                        previewAccuracy = 32;
                        break;
                    default:
                        previewAccuracy = 4;
                        break;
                }
                
                String antiSwitchType = antialiasingType.getValue();
                
                if(null != antiSwitchType)switch (antiSwitchType) {
                    case "Super Sampling":
                        antialiasType = 1;
                        break;
                    case "Adaptive Sampling":
                        antialiasType = 2;
                        break;
                    case "Stochastic Sampling":
                        antialiasType = 3;
                        break;
                    default:
                        previewAccuracy = 1;
                        break;
                }
                
                if ("".equals(antialiasWidth.getText())){
                    antialiasFilterWidth = 1.0;
                }
                else{
                    antialiasFilterWidth = Double.parseDouble(antialiasWidth.getText());
                    
                    antialiasFilterWidth = Math.abs(antialiasFilterWidth);
                }
            }
            catch(Exception ex){}
            
            settingsPopup.hide();
        });
        
        render.setOnAction((ActionEvent) -> {
            alert.setTitle("Rendering...");
            
            if (antialiasing){
                //imageView.setImage(renderPreview(800, 800, view, previewAccuracy));
                
                switch(antialiasType){
                    case 1:
                        alert.setHeaderText("Rendering camera view with Super Sampling. Please wait...");
                        alert.show();
                        
                        imageView.setImage(Render.renderSuperSample(800, 800, view, antialiasingDepth, antialiasFilterWidth));
                        break;
                    case 2:
                        alert.setHeaderText("Rendering camera view with Adaptive Sampling. Please wait...");
                        alert.show();
                        
                        imageView.setImage(Render.renderAdaptiveSample(800, 800, view, antialiasingDepth, antialiasFilterWidth));
                        break;
                    case 3:
                        alert.setHeaderText("Rendering camera view with Stochastic(Monte Carlo) Sampling. Please wait...");
                        alert.show();
                        
                        imageView.setImage(Render.renderStochasticSample(800, 800, view, antialiasingDepth, antialiasFilterWidth));
                        break;
                }
            }
            else{
                //imageView.setImage(renderPreview(800, 800, view, previewAccuracy));
                
                alert.setHeaderText("Rendering camera view. Please wait...");
                alert.show();
                
                imageView.setImage(Render.render(800, 800, view));
            }
            
            alert.hide();
        });
        
        preview.setOnAction((ActionEvent) -> {
            alert.setTitle("Rendering...");
            alert.setHeaderText("Rendering preview. Please wait...");
            alert.show();
            
            imageView.setImage(Render.renderPreview(800, 800, view, previewAccuracy));
            
            alert.hide();
        });
        
        addMaterial.setOnAction((ActionEvent event) -> {
            materialView.objects[0].material = new Material(){{
                name = "Mirror";
                reflect = (Vector position) -> 1.0;
                diffuse = (Vector position) -> Colour.create(1.0, 1.0, 1.0);
                specular = (Vector position) -> Colour.create(0.5, 0.5, 0.5);
                specularWidth = 100;
            }};
            
            addMaterialPopup.show();
        });
        
        addObject.setOnAction((ActionEvent event) -> {
            cmbOP.getItems().clear();
            
            for(Material i : Materials.materials){
                cmbOP.getItems().add(i.name);
            }
            
            addObjectPopup.show();
        });
        
        addLight.setOnAction((ActionEvent) -> {
            addLightPopup.show();
        });
        
        editCamera.setOnAction((ActionEvent) -> {
            editCameraPopup.show();
        });
        
        settings.setOnAction((ActionEvent) -> {
            settingsPopup.show();
        });
        
        deleteObject.setOnAction((ActionEvent) -> {
            String temp = objectList.getSelectionModel().getSelectedItem();
            
            if(temp != null){
                for (AbsObject object : view.objects) {
                    if (temp.equals(object.name)) {
                        ArrayList<AbsObject> tempList = new ArrayList<>(Arrays.asList(view.objects));
                        tempList.remove(object);
                        view.objects = tempList.toArray(new AbsObject[0]);
                        break;
                    }
                }

                objectListItems.clear();

                for (AbsObject i : view.objects){
                    if(!"Floor".equals(i.name)){
                        objectListItems.add(i.name);
                    }
                }

                objectList.setItems(objectListItems);
            }
        });
        
        deleteLight.setOnAction((ActionEvent) -> {
            String temp = lightList.getSelectionModel().getSelectedItem();
            
            if(temp != null){
                for (LightSource lightSource : view.lightSource) {
                    if (temp.equals(lightSource.name)) {
                        ArrayList<LightSource> tempList = new ArrayList<>(Arrays.asList(view.lightSource));
                        tempList.remove(lightSource);
                        view.lightSource = tempList.toArray(new LightSource[0]);
                        break;
                    }
                }

                lightListItems.clear();

                for (LightSource i : view.lightSource){
                    lightListItems.add(i.name);
                }

                lightList.setItems(lightListItems);
            }
        });
        
        deleteMaterial.setOnAction((ActionEvent) -> {
            String temp = materialList.getSelectionModel().getSelectedItem();
            
            if(temp != null){
                for (Material material : Materials.materials) {
                    if (temp.equals(material.name)) {
                        ArrayList<Material> tempList = new ArrayList<>(Arrays.asList(Materials.materials));
                        tempList.remove(material);
                        Materials.materials = tempList.toArray(new Material[0]);
                        break;
                    }
                }

                materialListItems.clear();

                for (Material i : Materials.materials){
                    materialListItems.add(i.name);
                }

                materialList.setItems(materialListItems);
            }
        });
        
        alert.hide();
        
        primaryStage.setMaxHeight(885);//.setResizable(false);
        primaryStage.setMaxWidth(1025);
        
        primaryStage.setMinHeight(885);//.setResizable(false);
        primaryStage.setMinWidth(1025);
        
        primaryStage.show();
        
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.setTitle("Welcome");
        message.setHeaderText(
                "This ray tracer supports:\n"
                        + "   -Diffusion\n"
                        + "   -Specularity\n"
                        + "   -Reflection\n"
                        + "   -Super, Adaptive, and Stochastic(Monte Carlo) Antialiasing\n"
                        + "   -Camera manipulation(target and position)\n"
                        + "   -Custom materials\n"
                        + "   -Custom point source lighting\n"
                        + "   -Custom objects(currently only a sphere)\n"
                        + "   -Render previews\n"
                        + "   -Inward only refraction(no transparency)\n\n"
                        + "Press OK to begin."
        );
        message.setGraphic(null);
        message.initModality(Modality.NONE);
        message.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private void addLight(LightSource tempLight, String _name){
        int tempLength = view.lightSource.length + 1;
        
        LightSource[] tempLightSources = new LightSource[tempLength];
        
        System.arraycopy(view.lightSource, 0, tempLightSources, 0, tempLength - 1);
        
        tempLightSources[tempLength - 1] = tempLight;
        
        view.lightSource = tempLightSources;
        
        lightListItems.add(_name);
    }
    
    private void addSphere(String _name, double x, double y, double z, double _radius, Material _material){
        int tempLength = view.objects.length + 1;
        
        AbsObject[] tempObjects = new AbsObject[tempLength];
        
        System.arraycopy(view.objects, 0, tempObjects, 0, tempLength - 1);
        
        Sphere tempSphere = new Sphere() {{
                    material = _material;
                    objectPosition = Vector.create(x, y, z);
                    radius = _radius;
                    name = _name;
                }};
        
        tempObjects[tempLength - 1] = tempSphere;
        
        view.objects = tempObjects;
        
        objectListItems.add(_name);
    }
    
    private void addMaterial(Material material, String _name){
        int tempLength = Materials.materials.length + 1;
        
        Material[] tempMat = new Material[tempLength];
        
        System.arraycopy(Materials.materials, 0, tempMat, 0, tempLength - 1);
        
        tempMat[tempLength - 1] = material;
        
        Materials.materials = tempMat;
        
        materialListItems.add(_name);
    }
    
    public static void setImage(BufferedImage bImage){
        Image image = SwingFXUtils.toFXImage(bImage, null);
        
        imageView.setImage(image);
    }
}