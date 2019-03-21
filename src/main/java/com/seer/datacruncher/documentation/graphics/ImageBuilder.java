/*
 * Copyright (c) 2019  Altimari Mario
 * All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.seer.datacruncher.documentation.graphics;

import com.seer.datacruncher.constants.DateTimeType;
import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ChecksTypeEntity;
import com.seer.datacruncher.jpa.entity.MacroEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.documentation.shared.XSDElement;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ImageBuilder implements DaoSet{
    private static Logger log = Logger.getLogger(ImageBuilder.class);
    private int x = 10;
    private int y = 10;
    private int w_x = 0;
    private int h_y = 0;
    private boolean newLevel = false;
    private boolean isXML = false;
    private boolean isJson = false;
    private boolean isLevel = true;
    private Graphics2D image = null;
    private FontMetrics fontMetrics = null;
    private File pngFile = null;
    private OutputStream os = null;
    private long idSchema = 0;
    private long maxSize = 0;
    private boolean elemFather = false;
    private XSDElement head = null;
    private BufferedImage buffer = null;
    private Font fontField = new Font("courrier", Font.BOLD, 16);
    private Font fontTitle = new Font("courrier", Font.ITALIC , 15);
    private Font fontDescr = new Font("courrier", Font.PLAIN, 15);

    public ImageBuilder(File file) {
        pngFile = file;
        try {
            os = new FileOutputStream(pngFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ImageBuilder(OutputStream outstream) {
        os = outstream;
    }
    public ImageBuilder() {
        pngFile = new File("C:\\xsd2png.png");
        try {
            os = new FileOutputStream(pngFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void setSize(List<String> lsProp,boolean isRoot) {
        buffer = new BufferedImage(1000, 100,BufferedImage.TYPE_INT_ARGB);
        image = buffer.createGraphics();
        image.setPaint(Color.black);
        image.setFont(fontField);
        fontMetrics = image.getFontMetrics();
        List<SchemaFieldEntity> lsAttrs = new ArrayList<SchemaFieldEntity>();

        String elName = null;
        long elSize = 0;
        if(!this.isLevel)
            this.maxSize = getFirstLevSize(head, 0) ;

        if(lsProp != null){
            drawProp(x, y,lsProp,maxSize,lsAttrs);
        }
        lsProp=new ArrayList<String>();
        drawElement(head, -1, lsProp, elName,elSize,isRoot,lsAttrs);
        if (y==10){
            h_y = 100;
        }else{
            h_y = y;
        }
    }
    public void createImage(List<String> lsProp,boolean isRoot) {
        buffer = new BufferedImage((this.w_x + 150), (this.h_y + 100),BufferedImage.TYPE_INT_ARGB);
        image = buffer.createGraphics();
        image.setColor(Color.white);
        image.fillRect(0, 0, this.w_x * 2,this.h_y * 2);
        if (this.maxSize> this.w_x)
            this.maxSize = this.w_x;
        this.w_x = 0;
        this.h_y = 0;
        image.setPaint(Color.black);
        image.setFont(fontField);
        fontMetrics = image.getFontMetrics();

        List<SchemaFieldEntity> lsAttrs = new ArrayList<SchemaFieldEntity>();
        String elName = null;
        long elSize = 0;
        if(lsProp != null){
            drawProp(x, y,lsProp,maxSize,lsAttrs);
        }
        lsProp=new ArrayList<String>();
        drawElement(head, -1, lsProp, elName,elSize,isRoot,lsAttrs);
        h_y = y;
    }
    public long getFirstLevSize(XSDElement element,  int level){
        long size =0;
        long elSize =0;
        boolean isElement = true;
        XSDElement firstChild;
        while (isElement){
            if ((element.getInnerElements() != null) && !element.getInnerElements().isEmpty()){
                firstChild = element.getInnerElements().get(0);
                if ("element".equals(firstChild.getType())) {
                    for (XSDElement child : element.getInnerElements()) {
                        elSize = fontMetrics.stringWidth(child.getName()) + 70;
                        if (elSize > size )
                            size = elSize;
                    }
                    isElement = false;
                }else {
                    element =  firstChild;
                }
            }else{
                isElement = false;
            }
        }
        if (size > 0){
           this.maxSize = size;
        }else{
            this.isLevel = true;
        }
        return size;
    }
    public void buildImage(long idSchema,XSDElement head) {
        List<String> lsProp=null;
        boolean isRoot= true;
        this.idSchema = idSchema;
        this.head = head;
        SchemaEntity schemaEntity = schemasDao.find(idSchema);
        int idStreamType = schemaEntity.getIdStreamType();
        if (idStreamType == StreamType.XML || idStreamType == StreamType.XMLEXI) {
            this.isXML =true;
            this.newLevel = false;
        }else{
            isRoot= false;
            if (idStreamType == StreamType.JSON)
                this.isJson =true;
            this.newLevel = true;
            this.x = 20;
            XSDElement element = head;
            XSDElement xsdElement = (element.getInnerElements().get(0)).getInnerElements().get(0);
            if (xsdElement.getType().equals("sequence")||xsdElement.getType().equals("all")) {
                this.head = xsdElement;
                this.isLevel = false;
            }
            @SuppressWarnings("rawtypes")
            List macros = macrosDao.read(idSchema).getResults();
            if (macros.size() > 0) {
                lsProp=new ArrayList<String>();
                lsProp.add("Macro: -");
            }
        }
        setSize(lsProp,isRoot);
        if(isXML){
            this.x = 10;
            this.newLevel = false;
        }else{
            this.x = 20;
            this.newLevel = true;
        }
        this.y = 10;

        this.image = null;
        this.fontMetrics = null;
        this.pngFile = null;
        this.elemFather = false;
        createImage(lsProp,isRoot);
        try {
            ImageIO.write(buffer, "PNG", os);
        } catch (IOException e) {
            log.error("Error creating the image.");
            e.printStackTrace();
        }
    }

    private void drawElement(XSDElement element, int y1,List<String> lsProp,String elName,long elSize, boolean isRoot,List<SchemaFieldEntity> lsAttrs) {
        int xx = 0;
        int yy = 0;

        if ("element".equals(element.getType())) {
            lsProp = new ArrayList<String>();
            lsAttrs = new ArrayList<SchemaFieldEntity>();
            xx = x;
            yy = y;
            elName = element.getName();
            elSize = x;
            addProp(elName,lsProp,isRoot,lsAttrs);

        }


        if ((element.getInnerElements() != null) && !element.getInnerElements().isEmpty()) {
            int _x = x;
            int _y = y;

            if ("element".equals(element.getType())) {
                if (newLevel) {
                    drawBridge(x, y + 23, y1);
                    newLevel = false;
                }
            }

            if (element.getType().equals("sequence")
                ||  element.getType().equals("choice")
                || element.getType().equals("all")
                    ) {
                if(this.isXML){
                    drawChildConn((int)elSize, y,elName);
                    drawBridge(x, y + 23, -1);
                    drawSquence(x, y + 18);
                    elemFather = true;
                }else if(this.isJson){
                    if (y1 == -1){
                        elemFather = false;
                    }else{
                        drawChildConn((int)elSize, y,elName);
                        drawBridge(x, y + 23, -1);
                        drawSquence(x, y + 18);
                        elemFather = true;
                    }
                }else{
                    elemFather = false;
                }
            /*
            } else if (element.getType().equals("choice")) {
                drawChildConn((int)elSize, y,elName);
                drawBridge(x, y + 23, -1);
                drawChoice(x, y + 18);
                elemFather = true;
            } else if (element.getType().equals("all")) {
                drawChildConn((int)elSize, y,elName);
                drawBridge(x, y + 23, -1);
                drawConnector(x, y + 18);
                elemFather = true;
            */
            } else if ("element".equals(element.getType())) {
                if (elemFather){
                    drawBridge(x, y + 23, -1);
                }
                drawSquare(x, y, element.getName(), false);
                elemFather = false;
            }else{
                elemFather = false;
            }

            for (XSDElement child : element.getInnerElements()) {
                int tot = element.getInnerElements().size();
                if(element.getInnerElements().size()>1) {
                    newLevel = true;
                }else{
                    newLevel = false;
                }
                drawElement(child, _y, lsProp,elName, elSize, false,lsAttrs);
                if (y1 == -1)
                    elemFather = false;
            }
            
            x = _x;
        } else {
            if ("element".equals(element.getType())) {
                if (newLevel) {
                    drawBridge(x, y + 23, y1);
                    newLevel = false;
                }
            }
            if (element.getType().equals("sequence")
                || element.getType().equals("choice")
                || element.getType().equals("all")
                    ) {
                //drawChildConn((int)elSize, y,elName);
                //drawSquence(x, y + 18);
                elemFather = true;
            /*
            } else if (element.getType().equals("choice")) {
                //drawChildConn((int)elSize, y,elName);
                //drawChoice(x, y + 18);
                elemFather = true;
            } else if (element.getType().equals("all")) {
                //drawChildConn((int)elSize, y,elName);
                //drawConnector(x, y + 18);
                elemFather = true;
            */
            } else if ("element".equals(element.getType())) {
                if (elemFather){
                    drawBridge(x, y + 23, -1);
                }
                drawSquare(x, y, element.getName(), true);
                elemFather = false;
            }else{
                elemFather = false;
            }
        }
        if ("element".equals(element.getType())) {
            if(isLevel)
                maxSize = fontMetrics.stringWidth(elName) + 70;
            drawProp(xx, yy,lsProp,maxSize,lsAttrs);
        }

    }


    private void drawSquare(int x, int y, String message, boolean isLeaf) {
        image.setFont(fontField);
        fontMetrics = image.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(message) + 20;

        image.draw3DRect(x, y, stringWidth + 25, 45, true);
        image.drawString(message, x + 10, y + 27);

        image.setFont(fontDescr);
        fontMetrics = image.getFontMetrics();
        if (isLeaf) {
            this.y = y + 70;
        } else {
            this.x = x + stringWidth + 55;

        }
        int newX = x + stringWidth + 55;
        if(newX>this.w_x)
            this.w_x = newX;
    }

    private void drawChildConn(int x, int y, String message) {
        if (message== null)
            message="";
        image.setFont(fontField);
        fontMetrics = image.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(message) + 20;
        //disegna il connettore per il figlio
        image.draw3DRect(x + stringWidth + 17, y + 15, 15, 15, true);
        image.drawLine(x + stringWidth + 17, y + 23, x + stringWidth + 32, y + 23);
        image.setFont(fontDescr);
        fontMetrics = image.getFontMetrics();
    }

    private void drawConnector(int x, int y) {
        String message = "All";
        image.drawLine(x, y, x + 10, y - 10);             // 1
        image.drawLine(x, y, x, y + 10);                 // 2
        image.drawLine(x, y + 10, x + 10, y + 20);      // 3
        image.drawLine(x + 10, y - 10, x + 40, y - 10); // 8
        image.drawLine(x + 10, y + 20, x + 40, y + 20); // 4
        image.drawLine(x + 40, y - 10, x + 50, y);         // 7
        image.drawLine(x + 50, y, x + 50, y + 10);        // 6
        image.drawLine(x + 40, y + 20, x + 50, y + 10); // 5
        image.drawLine(x + 10, y + 5, x + 40, y + 5);
        image.drawString(message, x + 15, y + 38);

        this.x = x + 75;
    }
    private void addFieldProp(SchemaFieldEntity field, List<String> lsProp) {
        String description = field.getDescription();
        String typedescr;
        if (description != null){
            description = Jsoup.parse(field.getDescription().trim()).text();
            if (!description.equals("")) {
                lsProp.add("Description:"+description);
            }
        }
        if(field.getIdFieldType() > 3){
            // field Type
            if(field.getIdFieldType() == FieldType.alphanumeric){
                lsProp.add("Type:Alphanumeric");
            }else if (field.getIdFieldType() == FieldType.numeric) {

                Integer fractionDigit = field.getFractionDigits();
                if (fractionDigit != null) {
                    lsProp.add("Type:Number - Decimal");
                }else{
                    lsProp.add("Type:Number - Integer");
                }
            }else if (field.getIdFieldType() == FieldType.date) {
                typedescr = "Type:Date - ";
                if (field.getIdDateTimeType() == DateTimeType.unixTimestamp){
                    typedescr = typedescr + "Timestamp unix";
                }else if (field.getIdDateTimeType()  == DateTimeType.xsdDate){
                    typedescr = typedescr + "XSD Date";
                }else if (field.getIdDateTimeType() == DateTimeType.xsdTime){
                    typedescr = typedescr + "XSD Time";
                }else if (field.getIdDateTimeType() == DateTimeType.xsdDateTime){
                    typedescr = typedescr + "XSD Date and Time";
                }else{
                    if (field.getIdDateType()!= null) {
                        switch (field.getIdDateType()) {
                            case DateTimeType.slashDDMMYYYY :  // dd/MM/yyyy
                                typedescr = typedescr + "dd/MM/yyyy";
                                break;
                            case DateTimeType.signDDMMYYYY :  // dd-MM-yyyy
                                typedescr = typedescr + "dd-MM-yyyy";
                                break;
                            case DateTimeType.dotDDMMYYYY:  // dd.MM.yyyy
                                typedescr = typedescr + "dd.MM.yyyy";
                                break;
                            case DateTimeType.DDMMYYYY:  // ddMMyyyy
                                typedescr = typedescr + "ddMMyyyy";
                                break;
                            case DateTimeType.slashDDMMYY:  // dd/MM/yy
                                typedescr = typedescr + "dd/MM/yy";
                                break;
                            case DateTimeType.signDDMMYY:  // dd-MM-yy
                                typedescr = typedescr + "hh:mm:ss";
                                break;
                            case DateTimeType.dotDDMMYY:  // dd.MM.yy
                                typedescr = typedescr + "dd.MM.yy";
                                break;
                            case DateTimeType.DDMMYY:  // ddMMyy
                                typedescr = typedescr + "ddMMyy";
                                break;
                            case DateTimeType.slashYYYYMMDD:  // yyyy/MM/dd
                                typedescr = typedescr + "yyyy/MM/dd";
                                break;
                            case DateTimeType.signYYYYMMDD:  // yyyy-MM-dd
                                typedescr = typedescr + "yyyy-MM-dd";
                                break;
                            case DateTimeType.dotYYYYMMDD:  // yyyy.MM.dd
                                typedescr = typedescr + "yyyy.MM.dd";
                                break;
                            case DateTimeType.YYYYMMDD:  // yyyyMMdd
                                typedescr = typedescr + "yyyyMMdd";
                                break;
                        }
                    }
                    if (field.getIdTimeType() !=  null){
                        if (field.getIdDateType()!= null)
                            typedescr = typedescr + " ";
                        switch (field.getIdTimeType()) {
                            case DateTimeType.dblpnthhmmss:  // hh:mm:ss
                                typedescr = typedescr + "hh:mm:ss";
                                break;
                            case DateTimeType.dothhmmss:  // hh.mm.ss
                                typedescr = typedescr + "hh.mm.ss";
                                break;
                            case DateTimeType.dblpnthhmm:  // hh:mm
                                typedescr = typedescr + "hh:mm";
                                break;
                            case DateTimeType.dothhmm:  // hh.mm
                                typedescr = typedescr + "hh.mm";
                                break;
                            case DateTimeType.dblpntZhhmmss:  // hh:mm:ss AM/PM
                                typedescr = typedescr + "hh:mm:ss AM/PM";
                                break;
                            case DateTimeType.dotZhhmmss:  // hh.mm.ss AM/PM
                                typedescr = typedescr + "hh.mm.ss AM/PM";
                                break;
                        }
                    }
                }
                lsProp.add(typedescr);
            }

            if (field.getIdFieldType() != FieldType.date) {
                // min/max
                typedescr = "";
                Integer minLength = field.getMinLength();
                if (minLength != null)
                    typedescr = " Min "+ minLength;

                Integer maxLength = field.getMaxLength();
                if (maxLength != null) {
                    if (minLength != null)
                        typedescr = typedescr +" - ";

                    typedescr = typedescr +"Max "+maxLength;
                }
                if (typedescr.length()>0)
                    lsProp.add("Length:" + typedescr);

            }

            // field Optional
            if (field.getNillable()) {
                lsProp.add("Optional:true");
            }

            // field Forecast
            if (field.getIsForecastable()) {
                typedescr = "";
                typedescr = typedescr + "Speed "+ field.getForecastSpeed()+" - Accuracy "+field.getForecastAccuracy();
                lsProp.add("Forecast:"+typedescr);
            }

            if (field.getIdCheckType() != 0){
                ChecksTypeEntity checksTypeEntity = checksTypeDao.getChecksTypeById(field.getIdCheckType());
                lsProp.add("Extra Check:"+checksTypeEntity.getName());
            }
        }
    }
    private void addProp(String elName,List<String> lsProp,boolean isroot, List<SchemaFieldEntity> lsAttrs) {
        SchemaFieldEntity field = schemaFieldsDao.getSchemaAllFieldByName(idSchema, elName);

        if (isXML || field != null ) {
            addFieldProp(field,lsProp);
        }
        if (isXML && field != null ) {
            lsAttrs.addAll(schemaFieldsDao.listAttrChild(idSchema,field.getIdSchemaField()));
            //lsAttrs = schemaFieldsDao.listAttrChild(idSchema,field.getIdSchemaField());
        }
        if (isroot){
            @SuppressWarnings("rawtypes")
            List macros = macrosDao.read(idSchema).getResults();
            if (macros.size() > 0) {
                lsProp.add("Macro: -");
            }
        }
    }
    private int getMaxSize(String value,long mSize){
        long lenVal = fontMetrics.stringWidth(value);
        Float parts = new Float((lenVal/1.0)/(mSize/1.0));
        parts = (float) (parts + 0.5);
        int textLen = value.length();
        int maxSize = textLen/Math.round(parts);
        return maxSize;
    }

    private long drawStrProp(String [] aValue, long _w , long _h ,long mSize) {
        int arrLen = 2;
        for(int i=0; i<arrLen; i++){
            String _value =aValue [i];
            if (i == 0){
                image.setFont(fontTitle);
                fontMetrics = image.getFontMetrics();
            }else{
                image.setFont(fontDescr);
                fontMetrics = image.getFontMetrics();
            }

            _h = _h + 18;
            long lenVal = fontMetrics.stringWidth(_value);
            if (lenVal <= mSize){
                image.drawString(_value, _w , _h);
                if (i == 0){
                    image.drawLine((int)_w, (int)_h+1,(int)(_w+fontMetrics.stringWidth(_value)),(int) _h+1);
                }
            }else{
                int textLen = _value.length();
                int maxSize = getMaxSize(_value,mSize);
                int si = 0;
                int ei = maxSize;
                String msg;

                while (si < textLen) {
                    if (ei > textLen) ei = textLen;

                    List<Object> lsMsg = formatMsg (_value, si,ei,mSize);
                    msg =((String) lsMsg.get(0)).trim();
                    ei = (Integer) lsMsg.get(1);
                    image.drawString(msg, _w , _h);
                    if (i == 0){
                        image.drawLine((int)_w, (int)_h+1,(int)(_w+fontMetrics.stringWidth(_value)),(int) _h+1);
                    }
                    _h = _h + 18;
                    si = ei;
                    ei = ei + maxSize;
                }
                _h = _h - 18;
            }
        }
        image.setFont(fontField);
        fontMetrics = image.getFontMetrics();
        return _h;
    }

    private List<Object> formatMsg (String value, int fInd, int lInd,long mSize){
        List<Object> lsMsg = new ArrayList<Object>();
        long lenVal;
        int spz ;
        boolean checkMin = true;
        String _msg = value.substring(fInd, lInd);
        String intValue = value.substring(0, lInd);
        lenVal = fontMetrics.stringWidth(_msg);

        if(lInd >=value.length() || Character.isWhitespace(value.charAt(lInd-1))){
            if (lenVal < mSize){
                lsMsg.add(0, _msg);
                lsMsg.add(1, lInd);
            }else{
                if(Character.isWhitespace(value.charAt(lInd-1)))
                    intValue = value.substring(0, lInd-1);
                spz = intValue.lastIndexOf(" ");
                if (spz > 0){
                    if(spz > fInd){
                        _msg = value.substring(fInd, spz);
                        lsMsg.add(0, _msg);
                        lsMsg.add(1, spz+1);
                    }else {
                        lsMsg.add(0, _msg);
                        lsMsg.add(1, lInd);
                    }
                }
            }
        }else {
            spz = value.indexOf(" ", lInd);
            if (spz > 0){
                _msg = value.substring(fInd, spz);
                lenVal = fontMetrics.stringWidth(_msg);
                if (lenVal < mSize){
                    lsMsg.add(0, _msg);
                    lsMsg.add(1, spz+1);
                    checkMin = false;
                }
            }
            if (checkMin){
                spz = intValue.lastIndexOf(" ");
                while(spz > 0 ){
                    _msg = value.substring(fInd, spz);
                    lenVal = fontMetrics.stringWidth(_msg);
                    if (lenVal < mSize){
                        lsMsg.add(0, _msg);
                        lsMsg.add(1, spz+1);
                        spz = 0;
                    } else{
                        intValue = value.substring(0, spz);
                        spz = intValue.lastIndexOf(" ");
                    }
                }
            }
        }
        if (lsMsg.size()== 0){
            _msg = value.substring(fInd, lInd);
            lsMsg.add(0, _msg);
            lsMsg.add(1, lInd);
        }
        return lsMsg;
    }
    private void drawProp(int x, int yy,List<String> lsProp,long maxSize, List<SchemaFieldEntity> lsAttrs) {
        Iterator<String> it=lsProp.iterator();
        int _y = yy + 60;
        long stringWidth;
        String value;
        int _h = _y;
        int _w = x+10;
        boolean isMacro = false;
        boolean isProp = false;

        stringWidth = maxSize + 60;
        maxSize = maxSize + 50;
        if(lsProp.size()> 0){
            while(it.hasNext()){
                value= it.next();
                String [] aValue = new String[2];
                int li = value.indexOf(":")+1;
                aValue [0]= value.substring(0, li);
                aValue [1]= value.substring(li);
                if (aValue [0].equals("Macro:")){
                    isMacro = true;
                }else{
                    isProp = true;
                    _y = (int) drawStrProp(aValue, _w , _y, maxSize);
                }
            }
            if (isProp){
                float dash1[] = {5.0f};
                BasicStroke dashed = new BasicStroke(1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f, dash1, 0.0f);

                image.setStroke(dashed);
                image.draw(new Rectangle2D.Double(x + 5, _h, stringWidth, ((_y -_h) + 5)));

                BasicStroke none = new BasicStroke();
                image.setStroke(none);
                _y = _y + 18;
            }
        }
        if(lsAttrs.size()> 0){
            drawAttrs(x, (_y - 60), maxSize,stringWidth, lsAttrs);
        }
        if (isMacro){
            drawMacro( x , (_y - 60),maxSize,stringWidth);
        }
        if (_y > this.y){
            this.y =_y;
        }
    }
    private void drawAttrs(int x, int yy,long maxSize,long stringWidth, List<SchemaFieldEntity> lsAttrs) {
        int _y = yy + 60;
        int _h = _y;
        int _w = x + 10;
        Iterator<String> it;

        for (Object o : lsAttrs) {
            SchemaFieldEntity ent = (SchemaFieldEntity) o;
            List<String> lsPropAttrs= new ArrayList<String>();
            lsPropAttrs.add("Attribute Name:"+ent.getName());
            addFieldProp(ent, lsPropAttrs);
            it= lsPropAttrs.iterator();
            String value;
            if(lsPropAttrs.size()> 0){

                while(it.hasNext()){
                    value= it.next();
                    String [] aValue = new String[2];
                    int li = value.indexOf(":")+1;
                    aValue [0]= value.substring(0, li);
                    aValue [1]= value.substring(li);
                    _y = (int) drawStrProp(aValue, _w , _y, (maxSize));
                    
                }

                image.draw(new Rectangle2D.Double(x + 5, _h, stringWidth, ((_y -_h) + 5)));
                _y = _y + 18;
                
            }
            
            
        }
        if (_y > this.y){
            this.y =_y;
        }

    }
    private void drawMacro(int x, int yy,long maxSize, long stringWidth) {
        int _y = yy + 60;
        //long stringWidth;
        int _h = _y;
        int _w = x + 10;


        //maxSize = maxSize + 60;
        //stringWidth = maxSize;
        @SuppressWarnings("rawtypes")
        List macros = macrosDao.read(idSchema).getResults();
        if (macros.size() > 0) {
            String [] aValue = new String[2];
            for (Object o : macros) {
                MacroEntity ent = (MacroEntity) o;
                if (ent.getIsActive() == 1) {
                    aValue [0] = "Macro "+ ent.getName().trim() + " :";
                    aValue [1] = Jsoup.parse(ent.getDescription().trim()).text();
                    //_y = _y + 45;
                    _y = (int) drawStrProp(aValue, _w , _y, (maxSize));
                    float dash1[] = {5.0f};
                    BasicStroke dashed = new BasicStroke(1.0f,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER,
                            10.0f, dash1, 0.0f);

                    image.setStroke(dashed);
                    image.draw(new Rectangle2D.Double(x + 5, _h, stringWidth, ((_y -_h) + 5)));

                    BasicStroke none = new BasicStroke();
                    image.setStroke(none);
                    _y = _y + 18;
                    _h = _y;
                }
            }
            if (_y > this.y){
                this.y =_y;
            }
        }


    }
    private void drawSquence(int x, int y) {
        String message = "Sequence";
        //start creo bordo
        image.drawLine(x, y, x + 10, y - 10);           // 1
        image.drawLine(x, y, x, y + 10);                // 2
        image.drawLine(x, y + 10, x + 10, y + 20);      // 3
        image.drawLine(x + 10, y + 20, x + 40, y + 20); // 4
        image.drawLine(x + 40, y + 20, x + 50, y + 10); // 5
        image.drawLine(x + 50, y, x + 50, y + 10);      // 6
        image.drawLine(x + 40, y - 10, x + 50, y);      // 7
        image.drawLine(x + 10, y - 10, x + 40, y - 10); // 8
        //end creo bordo
        //inserisco messaggio interno
        image.drawString("...", x + 20, y + 5);
        image.drawString(message, x-10, y + 38);

        this.x = x + 75;
    }
    private void drawBridge(int x, int y, int y1) {
        if (y1 != -1) {
            //                                _
            // elemento di livello successivo |
            image.drawLine(x - 10, y, x, y);
            image.drawLine(x - 10, y, x - 10, y1 + 23); // verticale
        } else {
            //elemento sullo stesso livello   -
            image.drawLine(x - 25, y, x, y);
        }
    }

    private void drawChoice(int x, int y) {
        String message = "choice";
        image.drawLine(x, y, x + 10, y - 10);             // 1
        image.drawLine(x, y, x, y + 10);                 // 2
        image.drawLine(x, y + 10, x + 10, y + 20);      // 3
        image.drawLine(x + 10, y + 20, x + 40, y + 20); // 4
        image.drawLine(x + 40, y + 20, x + 50, y + 10); // 5
        image.drawLine(x + 50, y, x + 50, y + 10);        // 6
        image.drawLine(x + 40, y - 10, x + 50, y);         // 7
        image.drawLine(x + 10, y - 10, x + 40, y - 10); // 8

        image.draw3DRect(x + 45, y, 10, 10, true);
        image.drawLine(x + 30, y + 5, x + 55, y + 5);
        image.drawLine(x + 35, y, x + 35, y + 10);
        image.drawLine(x + 30, y, x + 35, y);
        image.drawLine(x + 30, y + 10, x + 35, y + 10);
        image.fillOval(x + 25, y - 2, 4, 4);
        image.fillOval(x + 25, y + 3, 4, 4);
        image.fillOval(x + 25, y + 8, 4, 4);
        image.drawLine(x + 10, y + 5, x + 15, y + 5);
        image.drawLine(x + 15, y + 5, x + 20, y);
        image.drawString(message, x + 5, y + 38);

        this.x = x + 75;
    }
}