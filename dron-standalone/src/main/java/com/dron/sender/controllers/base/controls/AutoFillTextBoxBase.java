package com.dron.sender.controllers.base.controls;

import java.util.Arrays;
import java.util.List;

import com.dron.sender.controllers.base.helpers.CellUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.StringConverter;

/**
 * This class is main Control class which extends from Control <br>
 * and also implements basic functions of the AutoFillTextBoxFactory<br>
 *
 * You can easily utilize the AutoFillTextBox in your application<br>
 */
public abstract class AutoFillTextBoxBase<T> extends Control {

	/**
	 * Default pref width for base TextField and ListView
	 */
	private static final double DEFAULT_PREF_WIDTH = 100.0d;

	/**
	 * Default pref height for base TextField and ListView
	 */
	private static final double DEFAULT_PREF_HEIGHT = 26.0d;

	/**
	 * Default limit count in the ListView
	 */
	private final int DEFAULT_LIMIT_COUNT = 3;

	public static final String FILE_END_JSON = ".json";

	/**
	 * Base control
	 */
	protected TextField textField;

	protected Popup popup;

	protected List<T> items;

	protected final StringConverter<T> converter = CellUtils
			.defaultStringConverter();

	public abstract ObservableList<T> searchItems(T value);

	public abstract void onSelectAction(T fileName);

	// --- value
	/**
	 * The value of this ComboBox is defined as the selected item if the input
	 * is not editable, or if it is editable, the most recent user action:
	 * either the value input by the user, or the last selected item.
	 */
	public ObjectProperty<T> valueProperty() {
		return value;
	}

	private ObjectProperty<T> value = new SimpleObjectProperty<T>(this, "value") {
		T oldValue;

		@Override
		protected void invalidated() {
			super.invalidated();
			T newValue = get();

			if ((oldValue == null && newValue != null) || oldValue != null
					&& !oldValue.equals(newValue)) {
				valueInvalidated();
			}

			oldValue = newValue;
		}
	};

	public final void setValue(T value) {
		valueProperty().set(value);
	}

	public final T getValue() {
		return valueProperty().get();
	}

	public AutoFillTextBoxBase() {
		getStyleClass().setAll("autofill-text");
		textField = new TextField();

		popup = new Popup();
		popup.setAutoHide(true);

		listen();
	}

	private void listen() {
		this.prefHeightProperty().addListener(
				(bservable, oldValue, newValue) -> {
					textField.setPrefHeight(newValue.doubleValue());
				});
		this.prefWidthProperty().addListener(
				(bservable, oldValue, newValue) -> {
					textField.setPrefWidth(newValue.doubleValue());
				});
		this.minHeightProperty().addListener(
				(bservable, oldValue, newValue) -> {
					textField.setMinHeight(newValue.doubleValue());
				});
		this.maxHeightProperty().addListener(
				(bservable, oldValue, newValue) -> {
					textField.setMaxHeight(newValue.doubleValue());
				});
		this.minWidthProperty().addListener(
				(bservable, oldValue, newValue) -> {
					textField.setMinWidth(newValue.doubleValue());
				});
		this.maxWidthProperty().addListener(
				(bservable, oldValue, newValue) -> {
					textField.setMaxWidth(newValue.doubleValue());
				});
	}

	/**
	 * A Popup containing Listview is trigged from this function This function
	 * automatically resize it's height and width according to the width of
	 * textbox and item's cell height
	 */
	public void showPopup(T newValue) {
		ListView<T> listView = new ListView<>();
		listView.setPrefWidth(textField.getWidth());

		listView.getSelectionModel().clearSelection();
		listView.getFocusModel().focus(-1);

		listView.setItems(searchItems(newValue));

		// Set ListView height
		if (listView.getItems().size() > getDefaultLimitCount()) {
			listView.setPrefHeight(getDefaultLimitCount()
					* getDefaultPrefHeight());
		} else {
			listView.setPrefHeight(listView.getItems().size()
					* getDefaultPrefHeight());
		}

		listView.setOnMouseReleased(event -> {
			textField.setText(converter.toString(listView.getSelectionModel()
					.getSelectedItem()));
			textField.requestFocus();
			textField.requestLayout();
			textField.end();
			popup.hide();
			onSelectAction(listView.getSelectionModel().getSelectedItem());
		});

		listView.setOnKeyReleased(event -> {
			switch (event.getCode()) {
				case ENTER:
					textField.setText(converter.toString(listView
							.getSelectionModel().getSelectedItem()));
					textField.requestFocus();
					textField.requestLayout();
					textField.end();
					popup.hide();
					onSelectAction(listView.getSelectionModel()
							.getSelectedItem());
					break;
				default:
					break;
			}
		});

		// This cell factory helps to know which cell has been selected so that
		// when ever any cell is selected the textbox rawText must be changed
		listView.setCellFactory(view -> {
			// A simple ListCell containing only Label
			final ListCell<T> cell = new ListCell<T>() {
				public void updateItem(T item, boolean empty) {
					super.updateItem(item, empty);
					setText(converter.toString(item));
				}
			};

			return cell;
		});
		popup.getContent().clear();
		popup.getContent().add(listView);

		// SHOWING THE POPUP JUST BELOW TEXTBOX
		popup.show(getWindow(),
				getWindow().getX() + textField.localToScene(0, 0).getX()
						+ textField.getScene().getX(), getWindow().getY()
						+ textField.localToScene(0, 0).getY()
						+ textField.getScene().getY() + getDefaultPrefHeight());
	}

	private Window getWindow() {
		return getScene().getWindow();
	}

	@Override
	public void requestFocus() {
		super.requestFocus();
		textField.requestFocus();
	}

	@Override
	public void setMinSize(double d, double d1) {
		super.setMinSize(d, d1);
		textField.setMinSize(d, d1);
	}

	@Override
	public void setPrefSize(double d, double d1) {
		super.setPrefSize(d, d1);
		textField.setPrefSize(d, d1);
	}

	@Override
	public void resize(double d, double d1) {
		super.resize(d, d1);
		textField.resize(d, d1);
	}

	@Override
	public void setMaxSize(double d, double d1) {
		super.setMaxSize(d, d1);
		textField.setMaxSize(d, d1);
	}

	@Override
	protected double computeMaxHeight(double width) {
		return Math.max(getDefaultPrefHeight(), textField.getHeight());
	}

	@Override
	protected double computePrefHeight(double width) {
		return Math.max(getDefaultPrefHeight(), textField.getPrefHeight());
	}

	@Override
	protected double computeMinHeight(double width) {
		return Math.max(getDefaultPrefHeight(), textField.getPrefHeight());
	}

	@Override
	protected double computePrefWidth(double height) {
		return Math.max(getDefaultPrefWidth(), textField.getPrefWidth());
	}

	@Override
	protected double computeMaxWidth(double height) {
		return Math.max(getDefaultPrefWidth(), textField.getPrefWidth());
	}

	@Override
	protected double computeMinWidth(double height) {
		return Math.max(getDefaultPrefWidth(), textField.getPrefWidth());
	}

	public void setItems(T[] items) {
		setItems(Arrays.asList(items));
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public TextField getTextBox() {
		return textField;
	}

	public Popup getPopup() {
		return popup;
	}

	public int getDefaultLimitCount() {
		return DEFAULT_LIMIT_COUNT;
	}

	public double getDefaultPrefHeight() {
		return DEFAULT_PREF_HEIGHT;
	}

	public double getDefaultPrefWidth() {
		return DEFAULT_PREF_WIDTH;
	}

	void valueInvalidated() {
		fireEvent(new ActionEvent());
	}

}