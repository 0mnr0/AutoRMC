package svl.auto.rightclick;

import com.sun.jna.Library;
import com.sun.jna.Native;
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


		// Создаем планировщик задач для кликов мыши
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			if (isCapsLockActive() && isGameFocused() && client.player != null && client.player.isAlive()) {
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
			}
		}, 0, 30, TimeUnit.MILLISECONDS); // Интервал 24 миллисекунды
	}


	public boolean isGameFocused() {
		return client != null && client.isWindowFocused();
	}

	public interface User32 extends Library {
		User32 INSTANCE = Native.load("user32", User32.class);
		int GetKeyState(int nVirtKey);
	}

	public static boolean isCapsLockActive() {
		// CAPS LOCK имеет виртуальный код 0x14
		return (User32.INSTANCE.GetKeyState(0x14) & 0x0001) != 0;
	}
}
