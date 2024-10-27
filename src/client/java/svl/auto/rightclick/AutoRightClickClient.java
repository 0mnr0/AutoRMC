package svl.auto.rightclick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AutoRightClickClient implements ClientModInitializer {
	private boolean isCapsLockActive = false;
	private Robot robot;
	MinecraftClient client = null;

	public void onInitializeClient() {
		System.setProperty("java.awt.headless", "false");
		KeyBinding capsLockKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.capslockmod.capslock",
				GLFW.GLFW_KEY_CAPS_LOCK,
				"key.categories.misc"
		));

		try {
			robot = new Robot(); // Инициализация Robot для эмуляции кликов мыши
		} catch (AWTException e) {
			e.printStackTrace();
			return; // Завершаем метод, если произошла ошибка
		}

		// Проверка состояния CAPS LOCK при запуске мода
		client = MinecraftClient.getInstance();
		try {
			isCapsLockActive = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace(); // Обработка исключения, если метод не поддерживается
		}

		// Создаем планировщик задач для кликов мыши
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			if (capsLockKeyBinding.wasPressed()) {
				isCapsLockActive = !isCapsLockActive;
			}

			if (isCapsLockActive && isGameFocused()) {
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
			}
		}, 0, 24, TimeUnit.MILLISECONDS); // Интервал 24 миллисекунды
	}


	public boolean isGameFocused() {
		return client != null && client.isWindowFocused();
	}
}
