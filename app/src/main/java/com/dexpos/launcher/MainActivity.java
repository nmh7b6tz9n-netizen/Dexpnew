package com.dexpos.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
  private final ArrayList<App> apps = new ArrayList<>();
  private int selected = 0;
  private int offset = 0;
  private HomeView view;
  static class App { String name, pkg; Drawable icon; Intent intent; App(String n,String p,Drawable d,Intent i){name=n;pkg=p;icon=d;intent=i;} }
  @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);loadApps();view=new HomeView();setContentView(view);}
  private void loadApps(){
    PackageManager pm=getPackageManager();
    LinkedHashMap<String,App> found=new LinkedHashMap<>();
    for(String cat:new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER,Intent.CATEGORY_LAUNCHER}){
      Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(cat);
      for(ResolveInfo r:pm.queryIntentActivities(q,0)){
        String pkg=r.activityInfo.packageName;
        if(pkg.equals(getPackageName())||found.containsKey(pkg))continue;
        Intent launch=new Intent(Intent.ACTION_MAIN).addCategory(cat).setClassName(pkg,r.activityInfo.name).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        found.put(pkg,new App(r.loadLabel(pm).toString(),pkg,r.loadIcon(pm),launch));
      }
    }
    apps.addAll(found.values());Collections.sort(apps,(a,b)->a.name.compareToIgnoreCase(b.name));
  }
  @Override public boolean onKeyDown(int code,android.view.KeyEvent event){
    if(code==KeyEvent.KEYCODE_DPAD_RIGHT){selected=Math.min(apps.size()-1,selected+1);adjust();return true;}
    if(code==KeyEvent.KEYCODE_DPAD_LEFT){selected=Math.max(0,selected-1);adjust();return true;}
    if(code==KeyEvent.KEYCODE_DPAD_CENTER||code==KeyEvent.KEYCODE_ENTER){open();return true;}
    return super.onKeyDown(code,event);
  }
  private void adjust(){if(selected<offset)offset=selected;if(selected>=offset+6)offset=selected-5;view.invalidate();}
  private void open(){if(apps.isEmpty())return;try{startActivity(apps.get(selected).intent);}catch(Exception e){android.widget.Toast.makeText(this,"Не удалось открыть приложение",0).show();}}
  class HomeView extends View {
    Paint p=new Paint(3); Canvas c; float scale;
    HomeView(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);setFocusable(true);}
    void fill(int color){p.setColor(color);p.setStyle(Paint.Style.FILL);p.clearShadowLayer();p.setShader(null);}
    void rect(float x,float y,float w,float h,float r,int color){fill(color);c.drawRoundRect(x,y,x+w,y+h,r,r,p);}
    void text(String s,float x,float y,int size,int color,boolean bold){fill(color);p.setTypeface(bold?Typeface.create("sans-serif",Typeface.BOLD):Typeface.create("sans-serif-light",Typeface.NORMAL));p.setTextSize(size);c.drawText(s,x,y,p);}
    @Override protected void onDraw(Canvas actual){super.onDraw(actual);scale=Math.min(getWidth()/1920f,getHeight()/1080f);c=actual;c.save();c.translate((getWidth()-1920*scale)/2,(getHeight()-1080*scale)/2);c.scale(scale,scale);
      p.setShader(new LinearGradient(0,0,1920,1080,new int[]{Color.rgb(9,20,43),Color.rgb(24,42,76),Color.rgb(5,11,26)},null,Shader.TileMode.CLAMP));c.drawRect(0,0,1920,1080,p);p.setShader(null);
      // Subtle luminous atmospheric shapes
      p.setShader(new RadialGradient(1490,290,680,new int[]{0x555B86CC,0x005B86CC},null,Shader.TileMode.CLAMP));c.drawCircle(1490,290,680,p);
      p.setShader(new RadialGradient(210,1050,700,new int[]{0x334B82BA,0x004B82BA},null,Shader.TileMode.CLAMP));c.drawCircle(210,1050,700,p);p.setShader(null);
      text("DEXP",96,108,34,0xFFF5F8FF,true);text("OS",215,108,34,0xFF9DB6E0,false);
      String clock=new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date());text(clock,1660,108,44,0xFFF4F7FF,false);
      text("Добро пожаловать",100,285,68,0xFFF6F8FF,true);
      text("Ваши приложения. Ваш экран.",103,343,30,0xFFB6C7E4,false);
      rect(96,416,1728,425,44,0x263D5883);rect(97,417,1726,423,43,0x1AFFFFFF);
      text("ПРИЛОЖЕНИЯ",132,481,25,0xFFAFC6EB,true);
      if(apps.isEmpty())text("Приложения не найдены",132,640,36,0xFFFFFFFF,false);
      for(int i=offset;i<Math.min(apps.size(),offset+6);i++){
        int slot=i-offset;float x=130+slot*277,y=533;boolean focus=i==selected;
        if(focus){fill(0x55B5D5FF);p.setShadowLayer(35,0,0,0xCC91BFFF);c.drawRoundRect(x-10,y-10,x+242,y+242,38,38,p);p.clearShadowLayer();}
        rect(x,y,222,222,32,focus?0xFFEDF5FF:0xB42C4266);
        Drawable d=apps.get(i).icon;
        if(d!=null){c.save();c.translate(x+49,y+42);d.setBounds(0,0,124,124);d.draw(c);c.restore();}
        String name=apps.get(i).name;if(name.length()>17)name=name.substring(0,16)+"…";
        text(name,x+8,y+267,23,focus?0xFFFFFFFF:0xFFC3D0E4,focus);
      }
      text("◀  ▶   Выбрать приложение",100,945,27,0xFFB4C6E2,false);
      text("OK   Открыть",100,993,25,0xFFB4C6E2,false);
      text("DEXP OS 2.0",1645,1000,23,0xFF8BA3C7,false);
      c.restore();postInvalidateDelayed(30000);
    }
    @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()==MotionEvent.ACTION_UP){float x=(e.getX()-(getWidth()-1920*scale)/2)/scale;float y=(e.getY()-(getHeight()-1080*scale)/2)/scale;if(y>520&&y<810){int n=(int)((x-130)/277);if(n>=0&&n<6&&offset+n<apps.size()){selected=offset+n;open();}}return true;}return true;}
  }
}
