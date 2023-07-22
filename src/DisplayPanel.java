import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.io.IOException;

public class DisplayPanel extends JPanel implements ActionListener, KeyListener {
	Timer frameTimer = new javax.swing.Timer(16, this);;
	Timer clocksPerSecondCheckTimer = new Timer(150,this);
	int ramPage = 0;
	int romPage = 0;
	
	int rightAlignHelper = Math.max(getWidth(), 1334);

	public Font courierNewBold;
	
	String ramPageString = "";
	String romPageString = "";

	public static Color bgColor = Color.blue;
	public static Color fgColor = Color.white;
	
	public DisplayPanel() {
		super(null);
		
		clocksPerSecondCheckTimer.start();
		frameTimer.start();
		setBackground(bgColor);
		//setPreferredSize(new Dimension(1936, 966));
		//setPreferredSize(new Dimension(1700, 736));



		try {
			courierNewBold = Font.createFont(Font.TRUETYPE_FONT,this.getClass().getClassLoader().getResourceAsStream("CascadiaMono.ttf")).deriveFont(16f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(courierNewBold);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			System.out.println("Error loading Courier Font!");
		}
		
		romPageString = EaterEmulator.rom.ROMString.substring(romPage*960,(romPage+1)*960);
		ramPageString = EaterEmulator.ram.RAMString.substring(ramPage*960,(ramPage+1)*960);
		
		this.setFocusable(true);
	    this.requestFocus();
		this.addKeyListener(this);
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
		g.setColor(fgColor);
		//g.drawString("Render Mode: paintComponent",5,15);
		
//		g.setColor(getBackground());
//		g.fillRect(0, 0, EaterEmulator.getWindows()[1].getWidth(), EaterEmulator.getWindows()[1].getHeight());
//      g.setColor(Color.white);
//      g.drawString("Render Mode: fillRect",5,15);
		
		rightAlignHelper = Math.max(getWidth(), 1334);
		
        //Title
        g.setFont(new Font("Calibri Bold", 50, 50));
        g.drawString("BE6502 Emulator", 40, 50);
        
        //Version
        g.setFont(courierNewBold);
        g.drawString("v"+EaterEmulator.versionString+" (c) Dylan Speiser", rightAlignHelper-564, EaterEmulator.options.data.WindowYSize-65);
        
        //Clocks
        g.drawString("Clocks: "+EaterEmulator.clocks, 40, 80);
        g.drawString("Speed: "+EaterEmulator.cpu.ClocksPerSecond+" Hz"+(EaterEmulator.slowerClock ? " (Slow)" : ""), 40, 110);
        g.drawString("Clock Step (secs): "+EaterEmulator.options.data.StepSizeSecs, 200,110);

        //PAGE INDICATORS
        g.drawString("(K) <-- "+ROMLoader.byteToHexString((byte)(romPage+0x80))+" --> (L)", rightAlignHelper-404, Math.max(getHeight()-91, EaterEmulator.options.data.WindowYSize-100));
        g.drawString("(H) <-- "+ROMLoader.byteToHexString((byte)ramPage)+" --> (J)", rightAlignHelper-724, Math.max(getHeight()-91, EaterEmulator.options.data.WindowYSize-100));
        
        //ROM
        g.drawString("ROM", rightAlignHelper-324, 130);
        drawString(g,romPageString, rightAlignHelper-459, 150);
        
        //Stack Pointer Underline
        if (ramPage == 1) {
        	g.setColor(new Color(0.7f,0f,0f));
        	g.fillRect(rightAlignHelper-708+36*(Byte.toUnsignedInt(EaterEmulator.cpu.stackPointer)%8), 156+23*((int)Byte.toUnsignedInt(EaterEmulator.cpu.stackPointer)/8), 25, 22);
        	g.setColor(fgColor);
        }
        
        //RAM
        g.drawString("RAM", rightAlignHelper-654, 130);
        drawString(g,ramPageString, rightAlignHelper-779, 150);
        
	
        //CPU
        g.drawString("CPU Registers:",50,140);
        g.drawString("A: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.cpu.a)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.cpu.a)+")", 35, 170);
        g.drawString("X: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.cpu.x)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.cpu.x)+")", 35, 200);
        g.drawString("Y: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.cpu.y)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.cpu.y)+")", 35, 230);
        g.drawString("Stack Pointer: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.cpu.stackPointer)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.cpu.stackPointer)+")", 35, 260);
        g.drawString("Program Counter: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Short.toUnsignedInt(EaterEmulator.cpu.programCounter)), 16)+" ("+ROMLoader.padStringWithZeroes(Integer.toHexString(Short.toUnsignedInt(EaterEmulator.cpu.programCounter)).toUpperCase(),4)+")", 35, 290);
        g.drawString("Flags:                ("+ROMLoader.byteToHexString(EaterEmulator.cpu.flags)+")", 35, 320);
        
        g.drawString("Absolute Address: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Short.toUnsignedInt(EaterEmulator.cpu.addressAbsolute)), 16)+" ("+ROMLoader.byteToHexString((byte)(EaterEmulator.cpu.addressAbsolute/0xFF))+ROMLoader.byteToHexString((byte)EaterEmulator.cpu.addressAbsolute)+")", 35, 350);
        g.drawString("Relative Address: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Short.toUnsignedInt(EaterEmulator.cpu.addressRelative)), 16)+" ("+ROMLoader.byteToHexString((byte)(EaterEmulator.cpu.addressRelative/0xFF))+ROMLoader.byteToHexString((byte)EaterEmulator.cpu.addressRelative)+")", 35, 380);
        g.drawString("Opcode: "+EaterEmulator.cpu.lookup[Byte.toUnsignedInt(EaterEmulator.cpu.opcode)]+" ("+ROMLoader.byteToHexString(EaterEmulator.cpu.opcode)+")", 35, 410);
        g.drawString("Cycles: "+EaterEmulator.cpu.cycles, 35, 440);
        
        int counter = 0;
        String flagsString = "NVUBDIZC";
        for (char c : ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.cpu.flags)),8).toCharArray()) {
        	g.setColor((c == '1') ? Color.green : Color.red);
        	g.drawString(String.valueOf(flagsString.charAt(counter)), 100+16*counter, 320);
        	counter++;
        }
        
        g.setColor(fgColor);
        //VIA
        g.drawString("VIA Registers:",50,490);
        g.drawString("PORT A: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.via.PORTA)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.via.PORTA)+")", 35, 520);
        g.drawString("PORT B: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.via.PORTB)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.via.PORTB)+")", 35, 550);
        g.drawString("DDR  A: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.via.DDRA)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.via.DDRA)+")", 35, 580);
        g.drawString("DDR  B: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.via.DDRB)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.via.DDRB)+")", 35, 610);
        g.drawString("   PCR: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.via.PCR)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.via.PCR)+")", 35, 640);
        g.drawString("   IFR: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.via.IFR)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.via.IFR)+")", 35, 670);
        g.drawString("   IER: "+ROMLoader.padStringWithZeroes(Integer.toBinaryString(Byte.toUnsignedInt(EaterEmulator.via.IER)), 8)+" ("+ROMLoader.byteToHexString(EaterEmulator.via.IER)+")", 35, 700);
        
        //Controls
		g.setColor(Color.YELLOW);
		if (!EaterEmulator.keyboardMode) {
	        g.drawString("Controls:", 350, 490);
	        g.drawString("C - Toggle Clock", 300, 520);
	        g.drawString("Space - Pulse Clock", 300, 550);
	        g.drawString("R - Reset", 300, 580);
	        g.drawString("S - Toggle Slower Clock", 300, 610);
	        g.drawString("I - Trigger VIA CA1", 300, 640);
			g.drawString("T - Step Clock", 300, 670);
		} else {
			g.drawString("Keyboard Mode Controls:", 35, 760);
			g.drawString("Typing a key will write that key code to the memory location "+EaterEmulator.options.KeyboardLocationHexLabel.getText().substring(3), 35, 790);
			g.drawString(" and trigger an interrupt.", 35, 820);
		}
		g.setColor(fgColor);
	}
	
	public static void drawString(Graphics g, String text, int x, int y) {
	    for (String line : text.split("\n"))
	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}

	public void resetGraphics() {
		bgColor = EaterEmulator.options.data.bgColor;
		fgColor = EaterEmulator.options.data.fgColor;
		
		// sets the size of the window contents, but not the window
		//this.setSize(EaterEmulator.options.data.WindowXSize,EaterEmulator.options.data.WindowYSize);
		//EaterEmulator.getWindows()[1].setSize(EaterEmulator.options.data.WindowXSize,EaterEmulator.options.data.WindowYSize);
	
		setBackground(bgColor);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(frameTimer)) {

			EaterEmulator.running = true;
			int rightOffSet = 0;
			
			ramPageString = EaterEmulator.ram.RAMString.substring(ramPage*960,(ramPage+1)*960);
			EaterEmulator.ROMopenButton.setBounds(rightAlignHelper-rightOffSet-450, 15, 125, 25);
			EaterEmulator.RAMopenButton.setBounds(rightAlignHelper-rightOffSet-450, 45, 125, 25);
			EaterEmulator.ShowLCDButton.setBounds(rightAlignHelper-rightOffSet-600, 15, 125, 25);
			EaterEmulator.ShowGPUButton.setBounds(rightAlignHelper-rightOffSet-600, 45, 125, 25);
			EaterEmulator.optionsButton.setBounds(rightAlignHelper-rightOffSet-750, 15, 125, 25);
			EaterEmulator.keyboardButton.setBounds(rightAlignHelper-rightOffSet-750, 45, 125, 25);
			this.repaint();

			if (!EaterEmulator.options.isVisible())
				this.requestFocus();
		} else if (e.getSource().equals(clocksPerSecondCheckTimer)) {
			EaterEmulator.cpu.timeDelta = System.nanoTime()-EaterEmulator.cpu.lastTime;
            EaterEmulator.cpu.lastTime = System.nanoTime();

            EaterEmulator.cpu.clockDelta = EaterEmulator.clocks - EaterEmulator.cpu.lastClocks;
            EaterEmulator.cpu.lastClocks = EaterEmulator.clocks;

            EaterEmulator.cpu.ClocksPerSecond = Math.round(EaterEmulator.cpu.clockDelta/((double)EaterEmulator.cpu.timeDelta/1000000000.0));
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (!EaterEmulator.keyboardMode) {
			//Control Keyboard Mode
			switch (arg0.getKeyChar()) {
				case 'l':
					if (romPage < 0x7f) {
						romPage+=1;
						romPageString = EaterEmulator.rom.ROMString.substring(romPage*960,(romPage+1)*960);
					} else {
						if (romPage > 0x7f) {
							romPage = 0x7f;
							romPageString = EaterEmulator.rom.ROMString.substring(romPage*960,(romPage+1)*960);
						}
					}
					break;
				case 'k':
					if (romPage > 0) {
						romPage-=1;
						romPageString = EaterEmulator.rom.ROMString.substring(romPage*960,(romPage+1)*960);
					}
					break;
				case 'j':
					if (ramPage < 0x7f) {
						ramPage+=1;
						ramPageString = EaterEmulator.ram.RAMString.substring(ramPage*960,(ramPage+1)*960);
						if (ramPage > 0x7f) {
							ramPage = 0x7f;
							ramPageString = EaterEmulator.ram.RAMString.substring(ramPage*960,(ramPage+1)*960);
						}
					}
					break;
				case 'h':
					if (ramPage > 0) {
						ramPage-=1;
						ramPageString = EaterEmulator.ram.RAMString.substring(ramPage*960,(ramPage+1)*960);
					}
					break;
				case 'r':
					EaterEmulator.cpu.reset();
					EaterEmulator.lcd.reset();
					EaterEmulator.via = new VIA();
					EaterEmulator.ram = new RAM();
					EaterEmulator.gpu.setRAM(EaterEmulator.ram);
					ramPageString = EaterEmulator.ram.RAMString.substring(ramPage*960,(ramPage+1)*960);

					if (EaterEmulator.debug)
						System.out.println("Size: "+this.getWidth()+" x "+this.getHeight());
					break;
				case ' ':
					EaterEmulator.cpu.clock();
					break;
				case 'c':
					EaterEmulator.clockState = !EaterEmulator.clockState;
					break;
				case 's':
					EaterEmulator.slowerClock = !EaterEmulator.slowerClock;
					break;
				case 'i':
					EaterEmulator.via.CA1();
					break;
				case 't':
					EaterEmulator.singleStep = !EaterEmulator.singleStep;
					// here we need to put the 1 step clock
					// could just write a loop to call 
					// EaterEmulator.cpu.clock();
					// pause for 2 seconds and then call it again
					break;
			}
		} else {
			//Typing Keyboard Mode
			Bus.write((short)EaterEmulator.options.data.keyboardLocation, (byte)arg0.getKeyChar());
			EaterEmulator.via.CA1();
		}
	}
}
