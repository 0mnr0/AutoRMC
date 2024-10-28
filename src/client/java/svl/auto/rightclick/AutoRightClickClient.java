package svl.auto.rightclick;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class AutoRightClickClient implements ClientModInitializer, ModMenuApi {
	static int clickTimeout = 30;
	static Timer clickTimer;
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	ScheduledFuture<?> clickTask; // Переменная для отслеживания текущей задачи

	public static void openConfigScreen() {
		MinecraftClient.getInstance().setScreen(ModConfigScreen.getConfigScreen(null));
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> ModConfigScreen.getConfigScreen(parent); // Указываем экран настроек
	}

	private static Robot robot;
	static MinecraftClient client = null;

	public void onInitializeClient() {
		System.setProperty("java.awt.headless", "false");

		try {
			robot = new Robot(); // Инициализация Robot для эмуляции кликов мыши
		} catch (AWTException e) {
			e.printStackTrace();
			return; // Завершаем метод, если произошла ошибка
		}

		// Проверка состояния CAPS LOCK при запуске мода
		client = MinecraftClient.getInstance();

		// Создаем планировщик задач для кликов мыши


		setClickTimeout(clickTimeout);
	}

	// Метод для обновления таймера с новым интервалом
	static void updateClickTimer() {
		if (clickTimer != null) {
			clickTimer.cancel(); // Останавливаем старый таймер
		}

		clickTimer = new Timer();
		clickTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (isCapsLockActive() && isGameFocused() && client.player != null && client.player.isAlive()) {
					robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				}
			}
		}, 0, clickTimeout);
	}

	static void setClickTimeout(int newTimeout) {
		clickTimeout = newTimeout;
		updateClickTimer(); // Перезапускаем таймер с новым интервалом
	}


	public static boolean isGameFocused() {
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
