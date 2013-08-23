package org.ow2.chameleon.everest.client;

import org.ow2.chameleon.everest.filters.ResourceFilters;
import org.ow2.chameleon.everest.services.Relation;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceFilter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.floor;

/**
 * Created with IntelliJ IDEA.
 * User: colin
 * Date: 21/08/13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class ParserQuery {

    private final static String IN = "in";

    private final static String LOWER_THAN = "lt";

    private final static String GREATER_THAN = "gt";

    private final static String STARTWITH = "start";

    private final static String ENDWITH = "end";

    private final static String CONTAINS = "contains";

    private final static String OR = "or";

    private final static String EXIST = "exist";

    private final static String TYPE = "type";

    private final static String CHILD = "child";

    private final static String RELATION = "relation";

    private final static String HASPARAMETER = "parameter";

    private final static String HASHASHREF = "href";

    private final static String HASNAME = "name";

    private final static String HASACTION = "action";

    ResourceFilter m_returnFilter ;

    private int pos;

    private int length;

    private char c ;

    private final String request ;


    /**
     * the last parsed parameter name
     */
    private String paramName;

    /**
     * the last parsed parameter name
     */
    private float value;

    /**
     * the last parsed parameter name
     */
    private String stringValue;


    /**
     * the last parsed parameter name
     */
    private  String specialFieldName ;

    /**
     * the last parsed special value name
     */
    private String specialValueName;


    /**
     * buffer
     */
    private StringBuffer buffer = new StringBuffer();

    public ParserQuery(String request){
        this.request = request;
        this.length = request.length();
    }


    private char readNext() {
        if (pos == length) {
            c = '\0';
        } else {
            c = request.charAt(pos++);
        }
        //   System.out.println("CHAR " + c);
        return c;
    }

    public ResourceFilter parseFilter() throws ParseException {
        readNext();
        if (c != '{'){
            error("Filter Must be start with { ");
        }
        return parseFilter(false,false);
    }

    private ResourceFilter parseFilter(boolean isSubParamFilter,boolean OrCondition) throws ParseException {
        ResourceFilter resourceFilter = null;
        boolean startDecodeParam = false;
        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case '$':
                    if(!startDecodeParam){
                        parseSpecialField(false);
                        if(specialFieldName.equals(OR)){

                            readNext();
                            if (c != '['){
                                error("Or condition must start by [, not" + c);
                            }
                            resourceFilter = parseFilter(false,true);


                        }else if(specialFieldName.equals(CHILD)){
                            parseValue(false,true,false);
                            if (floor(value) == value) {
                                resourceFilter = ResourceFilters.hasAtLeastChildren((int)value);
                            } else{
                                error(" Child value must be an integer ! ");
                            }
                        }else if(specialFieldName.equals(RELATION)){
                            // TODO
                        }else {

                        }

                        return resourceFilter;

                    }else{
                        buffer.append(c);
                        break;
                    }

                case':' :
                    endParameterName();
                    startDecodeParam = false;
                    resourceFilter = parseValue(false,false,true);
                    if (!isSubParamFilter){
                        break;
                    }else {
                        return resourceFilter;
                    }
                case'}':
                    if(!startDecodeParam){
                        if (pos == length){
                            m_returnFilter = resourceFilter;
                            return m_returnFilter;
                        }else{
                            error(" Invalid request form");
                        }
                    } else{
                        buffer.append(c);
                        break;
                    }

                case'{':
                    if(!startDecodeParam){
                        List<ResourceFilter> resourceFilterList = new ArrayList<ResourceFilter>();
                        boolean firstRound = true;
                        boolean endExpected = false;
                        boolean startFilter = true;

                        do {
                            if (firstRound){
                                firstRound = false;
                                resourceFilterList.add(parseFilter(true,false));
                                endExpected = true;
                            }
                            switch (readNext()){
                                case'\0':
                                    error("End no expected " );
                                case'{':
                                    if (!endExpected){
                                        startFilter = true;
                                        resourceFilterList.add(parseFilter(true,false));
                                        endExpected = true;
                                    }else {
                                        error("End Excepted in the place of " + c );
                                    }
                                    break;
                                case'}':
                                    if (startFilter){
                                        endExpected = true;
                                        startFilter = false;
                                        break;
                                    }else if (endExpected && !OrCondition){
                                        return ResourceFilters.and(resourceFilterList);
                                    } else {
                                        if (OrCondition){
                                            error(" Or condition must be closed by \']\', not" + c);
                                        } else{
                                            error(" Unexpected Character " + c);
                                        }
                                    }

                                case',':
                                    if (!startFilter) {
                                        endExpected = false;
                                        break;
                                    }
                                case']':
                                    if (OrCondition && endExpected){
                                        return ResourceFilters.or(resourceFilterList);
                                    }else {
                                        error(" Unexpected Character " + c);
                                    }

                                default:
                                    error(" Unexpected Character " + c);
                            }
                        }while (pos<length);

                        error("End no expected ");
                    } else{
                        buffer.append(c);
                        break;
                    }

                default:
                    startDecodeParam = true;
                    buffer.append(c);
            }
        }while (pos<length);
        error("Not match the end of the request");
        return null;

    }

    private void parseSpecialField(boolean subField) throws ParseException {
        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case':' :
                    if (subField){
                        endSpecialValueName();
                    }else{
                        endSpecialFieldName();
                    }

                    return;
                default:
                    buffer.append(c);
            }
        }while (pos<length);
    }

    private void endSpecialFieldName() throws ParseException {
        if (buffer.length() == 0) {
            error("Empty special field name");
            // try to recover: won't store the value
            specialFieldName = null;
        }
        if (buffer.toString().equals(OR)){

        }else if  (buffer.toString().equals(RELATION)){

        }else if  (buffer.toString().equals(CHILD)){

        } else{
            error("Invalid Special field Parameter name : " + buffer.toString());
            // try to recover: won't store the value
            specialFieldName = null;
        }
        specialFieldName = buffer.toString();
        buffer.setLength(0);

    }

    private ResourceFilter parseValueFilter(boolean subValueFilter,boolean OrCondition,boolean callByOR) throws ParseException {
        // TODO : in recursive filter must check if we try apply filter on same type ( string or number)
        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case '$':
                    ResourceFilter resourceFilter = null;
                    parseSpecialField(true);

                    if(specialValueName.equals(IN)){
                        resourceFilter = parseArrayValue();
                    }else if(specialValueName.equals(LOWER_THAN)){
                        parseValue(false,true,false);
                        resourceFilter = ResourceFilters.lowerThan(paramName,value);

                    }else if(specialValueName.equals(GREATER_THAN)){
                        parseValue(false,true,false);
                        resourceFilter = ResourceFilters.greaterThan(paramName,value);

                    }else if(specialValueName.equals(STARTWITH)){
                        parseValue(true,false,false);
                        resourceFilter = ResourceFilters.stringStartWith(paramName,stringValue);
                    }else if(specialValueName.equals(ENDWITH)){
                        parseValue(true,false,false);
                        resourceFilter = ResourceFilters.stringEndWith(paramName,stringValue);
                    }else if(specialValueName.equals(CONTAINS)){
                        parseValue(true,false,false);
                        resourceFilter = ResourceFilters.stringContains(paramName,stringValue);

                    }else if(specialValueName.equals(TYPE)){
                        //TODO
                    }else if(specialValueName.equals(OR)){
                        if (!callByOR){
                            readNext();
                            if (c != '['){
                                error("Or condition must start by [, not" + c);
                            }
                            resourceFilter = parseValueFilter(false,true,true);

                        }else{
                            error("Or condition can't contains other Or condition");
                        }
                    }else{
                        error("Invalid Special Value  name  ");
                    }

                    return resourceFilter;

                case '{':
                    if (!subValueFilter){
                        List<ResourceFilter> resourceFilterList = new ArrayList<ResourceFilter>();

                        boolean firstRound = true;
                        boolean endExpected = false;
                        boolean startFilter = true;

                        do {
                            if (firstRound){
                                firstRound = false;
                                resourceFilterList.add( parseValueFilter(true,false,callByOR));
                                endExpected = true;
                            }
                            switch (readNext()){
                                case'\0':
                                    error("End no expected " );
                                case'{':
                                    if (!endExpected){
                                        startFilter = true;
                                        resourceFilterList.add(parseValueFilter(true,false,callByOR));
                                        endExpected = true;
                                    }else {
                                        error("End Excepted in the place of " + c );
                                    }
                                    break;
                                case'}':
                                    if (startFilter){
                                        endExpected = true;
                                        startFilter = false;
                                        break;
                                    }else if (endExpected && !OrCondition){
                                        //hack the position
                                        pos --;
                                        return ResourceFilters.and(resourceFilterList);
                                    } else {
                                        if (OrCondition){
                                            error(" Or condition must be closed by \']\', not" + c);
                                        } else{
                                            error(" Unexpected Character " + c);
                                        }
                                    }

                                case',':
                                    if (!startFilter) {
                                        endExpected = false;
                                        break;
                                    }
                                case']':
                                    if (OrCondition && endExpected){
                                        return ResourceFilters.or(resourceFilterList);
                                    }else {
                                        error(" Unexpected Character " + c);
                                    }

                                default:
                                    error(" Unexpected Character " + c);
                            }
                        }while (pos<length);
                        error("End no expected ");
                    }else{
                        error("Sub value filter can't contains other sub value filter " );
                    }

                default:
                    error(" Unexpected Character " + c);
            }
        }while (pos<length);
        error("End no expected ");
        return null;
    }



    private void endSpecialValueName() throws ParseException {
        if (buffer.length() == 0) {
            error("Empty special field name");
// try to recover: won't store the value
            specialValueName = null;
        }
        if (buffer.toString().equals(IN)){

        }else if  (buffer.toString().equals(LOWER_THAN)){

        }else if  (buffer.toString().equals(GREATER_THAN)){

        }else if  (buffer.toString().equals(CONTAINS)){

        }else if  (buffer.toString().equals(STARTWITH)){

        }else if  (buffer.toString().equals(ENDWITH)){

        }else if  (buffer.toString().equals(OR)){

        }else if  (buffer.toString().equals(TYPE)){

        } else{
            error("Invalid Special Value  name : " + buffer.toString());
// try to recover: won't store the value
            specialValueName = null;
        }

        specialValueName = buffer.toString();
        buffer.setLength(0);

    }


    private ResourceFilter parseValue(boolean ensureString,boolean ensureNumber,boolean canBeExist) throws ParseException{
        ResourceFilter resourceFilter = null;
        boolean pointAlreadyDetected = false;
        boolean alreadyStartSingleValueDecode = false;

        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case'.':
                    if (!ensureString){
                        if (alreadyStartSingleValueDecode){
                            if (pointAlreadyDetected){
                                pointAlreadyDetected = true;
                                buffer.append(c);
                                break;
                            }else{
                                error("Two \'.\' in the number argument");
                            }
                        }else{
                            error("Number Can't begin with \'.\'");
                        }
                    }else {
                        error("Value must be a string");
                    }

                case '[' :
                    if (!alreadyStartSingleValueDecode){
                        /// TODO
                        return parseArrayValue();
                    } else{
                        error("Value is not a number");
                    }

                case '{' :
                    if (ensureString){
                        error("Value must be a string not a Subfilter");
                    }
                    if (ensureNumber){
                        error(" Value must be a number not a Subfilter");
                    }
                    if (!alreadyStartSingleValueDecode){
                        resourceFilter =  parseValueFilter(false,false,false);
                        readNext();
                        if (c != '}'){
                            error("Miss the closure condition on " + specialValueName);
                        }
                        return resourceFilter;
                    } else{
                        error("Value is not a number");
                    }
                case '$' :
                    if (ensureString){
                        error("Value must be a string not a special value" );
                    }
                    if (ensureNumber){
                        error(" Value must be a number not a special value");
                    }
                    if (!alreadyStartSingleValueDecode){
                        if (canBeExist){
                            parseExist();
                            return ResourceFilters.keyExist(paramName);
                        }  else{
                            error("Value can't be " + EXIST);
                        }
                    } else{
                        error("Value is not a number");
                    }
                case '\"':
                case '\'':
                    if (ensureNumber){
                        error(" Value must be a number not a String");
                    }
                    if (!alreadyStartSingleValueDecode){

                        parseStringSingleValue();
                        return ResourceFilters.equalsTo(paramName,stringValue);

                    } else{
                        error("Value is not a number");
                    }

                case'}':
                    endSingleValue();
                    //hack the position
                    pos --;
                    return ResourceFilters.equalsTo(paramName,value);
                default:
                    if (ensureString){
                        error("Value must be a string");
                    }
                    if (!(pointAlreadyDetected) && Character.isDigit(c)){
                        alreadyStartSingleValueDecode = true;
                        buffer.append(c);
                    }else {
                        error("Value is not a number");
                    }
            }
        }while (pos<length);
        return null;

    }

    private void endSingleValue() throws ParseException{
        if (buffer.length() == 0) {
            error("Empty value ");
        }
        value = Float.parseFloat(buffer.toString());
        buffer.setLength(0);

    }

    private void parseStringSingleValue() throws ParseException{
        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case '\"':
                case '\'':
                    endSingleStringValue();
                    return;
                default:
                    buffer.append(c);
                    break;
            }
        }while (pos<length);
    }

    private void parseExist() throws ParseException{
        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case'}':
                    endExist();
                    //hack the position
                    pos --;
                    return;
                default:
                    buffer.append(c);
                    break;
            }
        }while (pos<length);
    }

    private void endExist() throws ParseException{
        if (buffer.length() == 0) {
            error("Empty value ");
        }
        if (buffer.toString().equals(EXIST)){

            buffer.setLength(0);
        }   else{
            error(" SPECIAL VALUE MUST BE " + EXIST);
        }
    }

    private void endSingleStringValue() throws ParseException{
        if (buffer.length() == 0) {
            error("Empty string Value");

        }
        stringValue = buffer.toString();
        buffer.setLength(0);
    }

    private ResourceFilter parseArrayValue() throws ParseException{
        readNext();
        if (c != '['){
            error("Must be an Array");
        }
        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case '\"':
                case '\'':
                    return parseArrayStringValue();

                default:
                    if ( Character.isDigit(c)){
                        buffer.append(c);
                        return parseArrayNumericValue();
                    }else{
                        error(" Not an array of Number or String");
                    }
            }
        }while (pos<length);
        return null;
    }

    private ResourceFilter parseArrayStringValue()throws ParseException{
        parseStringSingleValue();
        List<ResourceFilter> returnResourceFilter = new ArrayList<ResourceFilter>();
        returnResourceFilter.add( ResourceFilters.equalsTo(paramName,stringValue));
        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case ',':
                    readNext();
                    if((c == '\"') ||(c == '\"')){
                        parseStringSingleValue();
                        returnResourceFilter.add( ResourceFilters.equalsTo(paramName,stringValue));
                        break;
                    }else {
                        error(" The array contain other thing that string");
                    }
                case ']':
                    return ResourceFilters.or(returnResourceFilter);

                default:
                    error(" Forbiden Caracter into the array " + c);

            }
        }while (pos<length);
        return null;
    }

    private ResourceFilter parseArrayNumericValue() throws ParseException{

        List<ResourceFilter> returnResourceFilter = new ArrayList<ResourceFilter>();

        do {
            switch (readNext()){
                case'\0':
                    error("End no expected " );
                case ',':
                    endSingleValue();
                    returnResourceFilter.add(ResourceFilters.equalsTo(paramName,value));
                    break;
                case ']':
                    return ResourceFilters.or(returnResourceFilter);

                default:
                    if ( Character.isDigit(c)){
                        buffer.append(c);
                    }
            }
        }while (pos<length);
        return null;
    }





    private void endParameterName() throws ParseException {
        if (buffer.length() == 0) {
            error("Empty parameter name");
// try to recover: won't store the value
            paramName = null;
        }
        paramName = buffer.toString();
        buffer.setLength(0);
    }

    private void error(String message) throws ParseException {
        error(message, pos - 1);
    }

    private void error(String message, int p) throws ParseException {
        throw new ParseException(message, p);
    }
}
