package weatherapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class WeatherStationGUI extends JFrame {
    private final JTextField cityTextField;
    // This represents the user input field for city.
    private final JLabel temperatureLabel;
    private final JLabel humidityLabel;
    private final JLabel windLabel;
    private final JLabel precipitationLabel;
    // JLabel Fields are all initialized.
    // They represent what quantity will be displayed next to them.

    int red = new Random().nextInt(255);
    int green = new Random().nextInt(255);
    int blue = new Random().nextInt(255);
    // Random parameters to generate new colour blends.
    Font labelFont = new Font("Arial", Font.BOLD, 14);
    // Font to be used on JLabels and JButton.
    Font titleFont = new Font("Verdana", Font.BOLD, 18);
    // Font to be used on titles of windows/frames.

    private void saveForecastToFile(String city, String forecastText) {
        // The purpose of this method is to save each forecast to the history file.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("forecast_history.txt", true))) {
            writer.write("==== " + city + " ====\n");
            writer.write(forecastText);
            writer.write("\n------------------------------\n\n");
        } catch (IOException e) {
            System.out.println("Error writing to file!" );
        } // A try-catch block is used to handle the IOException.
    }
    private ArrayList<String[]> loadForecastHistory() {
        // The purpose of this method is to read previously saved weather forecast data and output it.

        ArrayList<String[]> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("forecast_history.txt"))) {
            String line;
            String city = "";
            StringBuilder forecast = new StringBuilder();
            boolean inEntry = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("====")) {
                    if (!city.isEmpty() && !forecast.isEmpty()) {
                        history.add(new String[]{city, forecast.toString()});
                        forecast.setLength(0);
                    }
                    city = line.replace("====", "").trim();
                    inEntry = true;
                } else if (line.startsWith("------------------------------")) {
                    if (!city.isEmpty() && !forecast.isEmpty()) {
                        history.add(new String[]{city, forecast.toString()});
                        city = "";
                        forecast.setLength(0);
                        inEntry = false;
                    }
                } else if (inEntry) {
                    forecast.append(line).append("\n");
                }
            }

            if (!city.isEmpty() && !forecast.isEmpty()) {
                history.add(new String[]{city, forecast.toString()});
            }

        } catch (IOException e) {
            System.out.println("Error reading file!");
        } // A try-catch block is used to handle the IOException.

        return history; // The history is returned as an arraylist of the string arrays(of the weather data parameters).
    }
    private void showForecastHistoryTable() {
        // The purpose of this method is to reveal the forecast history of the user in a tabular format.

        ArrayList<String[]> history = loadForecastHistory();
        // The history is loaded.

        String[] columnNames = {"City", "Forecast Summary"};
        // This array represents column headers.
        String[][] rowData = new String[history.size()][2];
        // This array represents the data or values.

        for (int i = 0; i < history.size(); i++) {
            rowData[i] = history.get(i);
        }
        // This "for" loop puts all data from history into their respective columns.

        JTable table = new JTable(rowData, columnNames);
        table.setFillsViewportHeight(true);
        // Height of the JTable is set.
        JScrollPane scrollPane = new JScrollPane(table);
        // A scroll pane is added to allow for scrolling.


        JOptionPane.showMessageDialog(null, scrollPane, "Forecast History", JOptionPane.INFORMATION_MESSAGE);
        // The information is shown in a dialog.
    }
    // Constructor call is made below.
    public WeatherStationGUI() {
        // The JFrame window is modified below.
        setTitle("My Weather Station");
        // The title is set.
        setSize(500, 500);
        // The size is set.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        // It is made to use a FlowLayout.
        setFont(titleFont);
        setBackground(new Color(red, green, blue));


        // The following is some components for user input and displaying data.
        cityTextField = new JTextField(10);
        cityTextField.setFont(labelFont);
        cityTextField.setForeground(new Color(red, green, blue));
        cityTextField.setBorder(BorderFactory.createLineBorder(new Color(red, green, blue), 1));
        // A city text field is created to hold the name of the desired city.
        JButton getWeatherButton = new JButton("Get Current Weather");
        getWeatherButton.setFont(labelFont);
        getWeatherButton.setForeground(new Color(red, green, blue));
        getWeatherButton.setBackground(Color.WHITE);
        getWeatherButton.setFocusPainted(false);
        // This represents the button that will listen for the text input and output the current weather of the city.
        JButton getForecastButton = new JButton("Get Forecast");
        getForecastButton.setFont(labelFont);
        getForecastButton.setForeground(new Color(red, green, blue));
        /* This represents the button that will listen for the text input and output the weather forecast of the
           desired city. */
        JTextArea forecastArea = new JTextArea(10, 40);
        forecastArea.setFont(labelFont);
        forecastArea.setForeground(new Color(red, green, blue));
        forecastArea.setEditable(false);
        forecastArea.setLineWrap(true);
        forecastArea.setWrapStyleWord(true);
        // An anonymous class is used below to specify the "ActionEvent" of the "getForecastButton".
        getForecastButton.addActionListener(_ -> {
            String city = cityTextField.getText();
            // The text from the city text field is retrieved.

            try {
                OpenMeteoClient.updateForecast(city);
                // Retrieves the current forecast of the desired city.
                String[] times = OpenMeteoClient.getForecastTime();
                String[] temps = OpenMeteoClient.getForecastTemperature();
                StringBuilder sb = getStringBuilder(city, times, temps);

                forecastArea.setText(sb.toString());
                saveForecastToFile(city, forecastArea.getText()); // City and forecast information is saved to file.
            } catch (Exception ex) {
                forecastArea.setText("Forecast is N/A");
            } // A try-catch block is implemented to handle the Exception.
        });

        temperatureLabel = new JLabel("Temperature: ");
        temperatureLabel.setFont(labelFont);
        temperatureLabel.setForeground(new Color(red, green, blue));
        // Temperature label is created.
        humidityLabel = new JLabel("Humidity: ");
        humidityLabel.setFont(labelFont);
        humidityLabel.setForeground(new Color(red, green, blue));
        // Humidity label is created.
        windLabel = new JLabel("Wind: ");
        windLabel.setFont(labelFont);
        windLabel.setForeground(new Color(red, green, blue));
        // Wind label is created.
        precipitationLabel = new JLabel("Precipitation: ");
        precipitationLabel.setFont(labelFont);
        precipitationLabel.setForeground(new Color(red, green, blue));
        // Precipitation label is created.
         JLabel text_field_label = new JLabel("Enter city name: ");
         text_field_label.setFont(labelFont);
         text_field_label.setForeground(new Color(red, green, blue));
         // This JLabel represents the label of the city text field.
         JButton history_button = new JButton("View Recent");
         history_button.setFont(labelFont);
         history_button.setForeground(new Color(red, green, blue));
         // This button is what the user will press to reveal their recent history.

        // Adding all components to the window...
        add(text_field_label);
        add(cityTextField);
        add(getWeatherButton);
        add(getForecastButton);
        add(history_button);
        add(forecastArea);
        add(temperatureLabel);
        add(humidityLabel);
        add(windLabel);
        add(precipitationLabel);
        history_button.addActionListener(_ -> showForecastHistoryTable());
        // An actionListener is added to the history button.


        // An action listener is added to the "getWeatherButton".
        // The action event being listened for is what triggers the text being inputted.
        getWeatherButton.addActionListener(_ -> {
            cityTextField.getText();
            // The weather data is fetched in a new thread to avoid freezing the UI.
            new FetchWeatherData().execute();
        });
    }

    private static StringBuilder getStringBuilder(String city, String[] times, String[] temps) {
        String[] prep = OpenMeteoClient.getForecastPrecipitation();
        String[] winds = OpenMeteoClient.getForecastWind();
        String[] hums = OpenMeteoClient.getForecastHumidity();
                /* All the weather data parameters are put into String arrays after being retrieved using
                   WeatherApp.OpenMeteoClient. */

        StringBuilder sb = new StringBuilder("Forecast for " + city + ":\n\n");
        // A "StringBuilder" object is instantiated.

        for (int i = 0; i < Math.min(5, times.length); i++) {
            sb.append(String.format("Time: %s\n", times[i]));
            sb.append(String.format("  Temp: %s°C\n", temps[i]));
            sb.append(String.format("  Precipitation: %s mm\n", prep[i]));
            sb.append(String.format("  Wind: %s km/h\n", winds[i]));
            sb.append(String.format("  Humidity: %s%%\n\n", hums[i]));
        } /* This "for" loop ensures that a forecast is printed regardless of
             the amount of time periods available. */
        return sb;
    }

    // The SwingWorker class is used to handle Application Programming Interface (API) calls in the background.
    // A private class is created below.
    // Its purpose is to fetch the weather data without delaying the User interface.
    private class FetchWeatherData extends SwingWorker<Void, Void> {
        // "FetchWeatherData" is a subclass of "SwingWorker".
        private String temperature, humidity, wind, precipitation;

        @Override
        protected Void doInBackground() {
            String city = cityTextField.getText();
            // The weather data is fetched using "WeatherApp.OpenMeteoClient" methods below.
            try {
                OpenMeteoClient.updateCurrentWeather(city);
                temperature = OpenMeteoClient.getCurrentTemperature();
                humidity = OpenMeteoClient.getCurrentHumidity();
                wind = OpenMeteoClient.getCurrentWind();
                precipitation = OpenMeteoClient.getCurrentPrecipitation();
            } catch (Exception e) {
                temperature = "N/A";
                humidity = "N/A";
                wind = "N/A";
                precipitation = "N/A";
            } // A try-catch block is used to handle the Exception.
            return null;
        }

        @Override
        protected void done() {
            // This updates the GUI after the background task finishes
            try {
                temperatureLabel.setText("Temperature: " + temperature);
                humidityLabel.setText("Humidity: " + humidity);
                windLabel.setText("Wind: " + wind);
                precipitationLabel.setText("Precipitation: " + precipitation);
            } catch (Exception e) {
                temperatureLabel.setText("Error retrieving data.");
                humidityLabel.setText("Error retrieving data.");
                windLabel.setText("Error retrieving data.");
                precipitationLabel.setText("Error retrieving data.");
            } // A try-catch block is used to handle the Exception.
        }
    }

    // A main method is created below.
    public static void main(String[] args) {
        // The following creates and displays the GUI.
        SwingUtilities.invokeLater(() -> new WeatherStationGUI().setVisible(true));
        // A new "WeatherApp.WeatherStationGUI" object is created. It is made visible.
    }
}
