package translation;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {


            final Translator translator = new JSONTranslator();
            final CountryCodeConverter countryConv = new CountryCodeConverter();
            final LanguageCodeConverter langConv = new LanguageCodeConverter();


            JFrame frame = new JFrame("Country Name Translator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel main = new JPanel();
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


            JPanel langRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            langRow.add(new JLabel("Language:"));
            JComboBox<String> languageCombo = new JComboBox<>();
            langRow.add(languageCombo);


            JPanel transRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel transTitle = new JLabel("                       Translation: ");
            JLabel transValue = new JLabel("—");
            transRow.add(transTitle);
            transRow.add(transValue);


            DefaultListModel<String> countryModel = new DefaultListModel<>();

            final Map<String, String> countryNameToCode = new LinkedHashMap<>();
            for (String alpha3 : translator.getCountryCodes()) {
                String display = Optional.ofNullable(countryConv.fromCountryCode(alpha3)).orElse(alpha3);
                countryModel.addElement(display);
                countryNameToCode.put(display, alpha3);
            }
            JList<String> countryList = new JList<>(countryModel);
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            countryList.setVisibleRowCount(12);
            JScrollPane countryScroll = new JScrollPane(countryList);


            final Map<String, String> langNameToCode = new LinkedHashMap<>();
            languageCombo.removeAllItems();
            for (String code : translator.getLanguageCodes()) {
                String display = Optional.ofNullable(langConv.fromLanguageCode(code)).orElse(code);
                languageCombo.addItem(display);
                langNameToCode.put(display, code); // remember exact alpha-2
            }
            if (languageCombo.getItemCount() > 0) languageCombo.setSelectedIndex(0);


            Runnable refreshTranslation = () -> {
                String countryDisplay = countryList.getSelectedValue();
                String langDisplay = (String) languageCombo.getSelectedItem();

                if (countryDisplay == null || langDisplay == null) {
                    transValue.setText("—");
                    return;
                }

                String countryCode = countryNameToCode.get(countryDisplay);
                String langCode = langNameToCode.get(langDisplay);

                String result = (countryCode == null || langCode == null)
                        ? null
                        : translator.translate(countryCode, langCode);

                transValue.setText(result != null ? result : "no translation found!");
            };


            languageCombo.addActionListener(e -> refreshTranslation.run());
            countryList.addListSelectionListener((ListSelectionListener) e -> {
                if (!e.getValueIsAdjusting()) refreshTranslation.run();
            });


            main.add(langRow);
            main.add(Box.createVerticalStrut(8));
            main.add(transRow);
            main.add(Box.createVerticalStrut(8));
            main.add(countryScroll);

            frame.setContentPane(main);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);


            if (!countryModel.isEmpty()) {
                countryList.setSelectedIndex(0);
                refreshTranslation.run();
            }
        });
    }
}