package svl.auto.rightclick;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModConfigScreen {
    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("Настройка AutoRMC"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Добавление категории "Общие настройки"
        builder.getOrCreateCategory(Text.translatable("category.general"))
                .addEntry(entryBuilder.startIntField((Text) Text.translatable("Время между кликами (мс)"), 30)
                        .setDefaultValue(30)                    // Значение по умолчанию
                        .setMin(1)                           // Минимальное значение
                        .setMax(1000)                           // Максимальное значение
                        .setTooltip(Text.translatable("Задержка перед следующим нажатием (мс)"))
                        .setSaveConsumer(newValue -> {
                            System.out.println("newValueSaved: "+newValue);
                            AutoRightClickClient.clickTimeout = newValue;
                            AutoRightClickClient.setClickTimeout(newValue);
                            // Логика сохранения нового значения
                            // Например, YourModConfig.sliderValue = newValue;
                        })
                        .build());

        return builder.build();
    }
}
