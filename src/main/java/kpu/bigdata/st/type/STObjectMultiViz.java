package kpu.bigdata.st.type;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class STObjectMultiViz extends JFrame implements ActionListener{
	/*
	 * 
	 * 2개의 시공간 객체 시각화 도구
	 * 
	 * */
	
	// 시각화 할 시공간 객체
	// 1이 먼저 그려지고 그 다음 2가 그려짐
	final static int c[] = {0, 800};
	
	private STObject stGeometry1;
	private STObject stGeometry2;
	
	// 1초 전으로
	private JButton prev;
	
	// 1초 후로
	private JButton next;
	
	// 현재 시각(에 해당하는 키값)
	private int current = 0;
	
	// 현재 시각 표시
	private JLabel currentTime;
	
	// 공간객체가 그려지는 패널
	private JPanel canvas;
	
	// 시간값들
	private LinkedList<Long> keyList;
	

	public STObjectMultiViz(STObject stg1, STObject stg2) {
		this(stg1.getName()+"+"+stg2.getName(), stg1, stg2, c, c);
	}
	
	public STObjectMultiViz(String title, STObject stg1, STObject stg2) {
		this(title, stg1, stg2, c, c);
	}
	
	public STObjectMultiViz(String title, STObject stg1, STObject stg2, int c_w[], int c_h[]) {
		this.stGeometry1 = stg1;
		this.stGeometry2 = stg2;
		this.keyList = new LinkedList<>(stg1.getGeomtryMap().keySet());
		Collections.sort(keyList);
		setSize(800, 870);
		canvas = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Geometry geom1 = stGeometry1.getGeomtryMap().get(keyList.get(current));
				Geometry geom2 = stGeometry2.getGeomtryMap().get(keyList.get(current));
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.black);

				int x[] = new int[geom1.getCoordinates().length];
				int y[] = new int[geom1.getCoordinates().length];
				
				double w_rate = 800/(c_w[1]-c_w[0]);
				double h_rate = 800/(c_h[1]-c_h[0]);
				
				int i=0;
				g.setColor(Color.red);
				for(Coordinate c : geom1.getCoordinates()) {
					x[i] = (int)((c.x-c_w[0])*w_rate);
					y[i] = (int)((c.y-c_h[0])*h_rate);
					i++;
					g.fillOval((int)((c.x-c_w[0])*w_rate)-2, (int)((c.y-c_h[0])*h_rate)-2, 5, 5);
				}
				Polygon p = new Polygon(x, y, x.length);
				//g.fillPolygon(p);
				g.drawPolygon(p);
				

				g.setColor(Color.blue);
				int x2[] = new int[geom2.getCoordinates().length];
				int y2[] = new int[geom2.getCoordinates().length];
				i=0;
				for(Coordinate c : geom2.getCoordinates()) {
					x2[i] = (int)((c.x-c_w[0])*w_rate);
					y2[i] = (int)((c.y-c_h[0])*h_rate);
					i++;
					g.fillOval((int)((c.x-c_w[0])*w_rate)-2, (int)((c.y-c_h[0])*h_rate)-2, 5, 5);
				}
				Polygon p2 = new Polygon(x2, y2, x2.length);
				//g.fillPolygon(p2);
				g.drawPolygon(p2);
			}
		};
		canvas.setPreferredSize(new Dimension(800, 800));
		canvas.setBackground(Color.white);
		add(canvas, BorderLayout.CENTER);
		JPanel time = new JPanel();
		time.setPreferredSize(new Dimension(800, 70));
		add(time, BorderLayout.PAGE_END);
		currentTime = new JLabel(utils.getDateTimeString(keyList.get(current)*1000));
		prev = new JButton("< prev");
		next = new JButton("next >");
		prev.addActionListener(this);
		next.addActionListener(this);
		currentTime.setHorizontalAlignment(SwingConstants.CENTER);
		currentTime.setPreferredSize(new Dimension(700,20));
		prev.setPreferredSize(new Dimension(100, 30));
		prev.setEnabled(false);
		if(keyList.size() == 1){
			next.setEnabled(false);
		}
		next.setPreferredSize(new Dimension(100, 30));
		time.add(currentTime, BorderLayout.NORTH);
		time.add(prev, BorderLayout.SOUTH);
		time.add(next, BorderLayout.EAST);
		setTitle(title);
		setVisible(true);
	}

	// 공간 객체 그림
	public void update() {
		canvas.repaint();
		currentTime.setText(utils.getDateTimeString(keyList.get(current)*1000));
	}

	// 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("< prev")) {
			if(current > 0) {
				current--;
				update();
				if(current < keyList.size()-1) next.setEnabled(true);
				if(current <= 0) prev.setEnabled(false);
			}else {
				prev.setEnabled(false);
			}
		}
		else if(e.getActionCommand().equals("next >")) {
			if(current < keyList.size()-1) {
				current++;
				update();
				if(current > 0) prev.setEnabled(true);
				if(current >= keyList.size()-1) next.setEnabled(false);
			}else {
				next.setEnabled(false);
			}
		}
	}
}
