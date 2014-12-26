package com.dron.standalone.actions.services;

import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dron.standalone.actions.interfaces.ISceneService;
import com.dron.standalone.actions.interfaces.IStageService;
import com.dron.standalone.models.ControllerEnum;

@Component
public class StageService implements IStageService {

	private static final String DRON = "~ DRON ~";

	@Autowired
	private ISceneService iSceneService;

	private Stage primaryStage;

	public void initPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(DRON);
	}

	@Override
	public void showController(ControllerEnum controllerEnum) {
		primaryStage.setScene(iSceneService.findScene(controllerEnum));
		primaryStage.show();
	}

}