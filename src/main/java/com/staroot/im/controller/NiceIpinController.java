package com.staroot.im.controller;

import Kisinfo.Check.IPIN2Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nice/ipin")
public class NiceIpinController {
    private String sSiteCode	= "";		//  NICE평가정보에서 발급한 IPIN 서비스 사이트코드
    private String sSitePw		= "";		//  NICE평가정보에서 발급한 IPIN 서비스 사이트패스워드


    @GetMapping("/main")
    public String main(HttpServletRequest request,Model model) {
        /********************************************************************************************************************************************
         NICE평가정보 Copyright(c) KOREA INFOMATION SERVICE INC. ALL RIGHTS RESERVED

         서비스명 : IPIN 가상주민번호 서비스
         페이지명 : IPIN 가상주민번호 서비스 호출 페이지
         *********************************************************************************************************************************************/
	/*
	┌ sReturnURL 변수에 대한 설명  ─────────────────────────────────────────────────────
		암호화된 인증결과 데이터를 리턴받을 URL을 프로토콜부터 풀주소로 정의해주세요.

		* URL은 반드시 프로토콜부터 입력해야 하며 외부에서 접속이 가능한 주소여야 합니다.
		* 당사 샘플페이지 중 ipin_process 페이지가 인증결과 데이터를 리턴받는 페이지입니다.

		예 - http://www.test.co.kr/ipin_process.jsp
			 https://www.test.kr:4343/ipin_process.jsp
	└────────────────────────────────────────────────────────────────────
	*/
        String sReturnURL   = "http://localhost:8080/nice/ipin/process";


	/*
	┌ sCPRequest 변수에 대한 설명  ─────────────────────────────────────────────────────
		CP요청번호는 추가적인 보안처리를 위한 변수입니다. 인증 후 인증결과 데이터와 함께 전달됩니다.
		세션에 저장된 값과 비교해 데이터 위변조를 검사하거나, 사용자 특정용으로 이용할 수 있습니다.
		위변조 검사는 인증에 필수적인 처리는 아니며 보안을 위한 권고 사항입니다.

		+ CP요청번호 설정 방법
			1. 당사에서 배포된 모듈로 생성
			2. 귀사에서 임의로 정의(최대 30byte)
	└────────────────────────────────────────────────────────────────────
	*/
        String sCPRequest = "";


        // 모듈객체 생성
        IPIN2Client pClient = new IPIN2Client();

        // CP요청번호 생성
        sCPRequest = pClient.getRequestNO(sSiteCode);

        // CP요청번호 세션에 저장
        // : 저장된 값은 ipin_result 페이지에서 데이터 위변조 검사에 이용됩니다.
        HttpSession session = request.getSession();//별도추가

        session.setAttribute("CPREQUEST" , sCPRequest);


        // 인증요청 암호화 데이터 생성
        int iRtn = pClient.fnRequest(sSiteCode, sSitePw, sCPRequest, sReturnURL);

        String sEncData		= "";	// 인증요청 암호화 데이터
        String sRtnMsg		= "";	// 처리결과 메세지

        // 암호화 처리결과코드에 따른 처리
        if (iRtn == 0)
        {
            // 인증요청 암호화 데이터 추출
            sEncData = pClient.getCipherData();
            sRtnMsg = "정상 처리되었습니다.";
        }
        else if (iRtn == -1)
        {
            sRtnMsg = "암호화 시스템 오류 : 귀사 서버 환경에 맞는 모듈을 이용해주십시오.<br>오류가 지속되는 경우 iRtn 값, 서버 환경정보, 사이트코드를 기재해 문의주시기 바랍니다.";
        }
        else if (iRtn == -2)
        {
            sRtnMsg = "암호화 처리 오류 : 최신 모듈을 이용해주십시오. 오류가 지속되는 경우 iRtn 값, 서버 환경정보, 사이트코드를 기재해 문의주시기 바랍니다.";
        }
        else if (iRtn == -9)
        {
            sRtnMsg = "입력 정보 오류 : 암호화 함수에 입력된 파라미터 값을 확인해주십시오.<br>오류가 지속되는 경우, 함수 실행 직전 각 파라미터 값을 로그로 출력해 발송해주시기 바랍니다.";
        }
        else
        {
            sRtnMsg = "기타 오류: iRtn 값과 적용한 샘플소스를 발송해주시기 바랍니다.";
        }
        // 리턴값설정
        model.addAttribute("iRtn", iRtn);
        model.addAttribute("sRtnMsg", sRtnMsg);
        model.addAttribute("sEncData", sEncData);

        return "/nice/ipin/main";
    }

    @GetMapping("/process")
    public String process(HttpServletRequest request,Model model) {
        /*********************************************************************************************
         NICE평가정보 Copyright(c) KOREA INFOMATION SERVICE INC. ALL RIGHTS RESERVED

         서비스명 : IPIN 가상주민번호 서비스
         페이지명 : IPIN 가상주민번호 서비스 인증결과 처리 페이지

         리턴받은 인증결과 데이터를 호출 페이지로 전달하고 인증팝업을 닫는 페이지
         **********************************************************************************************/

        // 인증결과 암호화 데이터 취득 (인증요청 암호화 데이터 값과 달라야 정상)
        String sResponseData = requestReplace(request.getParameter("enc_data"), "encodeData");

        // 리턴값설정
        model.addAttribute("sResponseData", sResponseData);

        return "/nice/ipin/process";
    }


    @PostMapping("/result")
    public String result(HttpServletRequest request,Model model) {
        /********************************************************************************************************************************************
         NICE평가정보 Copyright(c) KOREA INFOMATION SERVICE INC. ALL RIGHTS RESERVED

         서비스명 : IPIN 가상주민번호서비스 서비스
         페이지명 : IPIN 가상주민번호서비스 결과 페이지
         *********************************************************************************************************************************************/
        // ipin_process에서 전달받은 인증결과 암호화 데이터 취득
        String sResponseData = requestReplace(request.getParameter("enc_data"), "encodeData");

        // ipin_main에서 세션에 저장한 CP요청번호 취득
        HttpSession session = request.getSession();//별도추가

        String sCPRequest = (String)session.getAttribute("CPREQUEST");

        // 모듈 객체 생성
        IPIN2Client pClient = new IPIN2Client();

        // 인증결과 데이터 복호화
        // : CP요청번호 파라미터 추가 시 세션 추출값과 전송된 데이터 비교해 데이터 위변조 검사 가능
        // 예) int iRtn = pClient.fnResponse(sSiteCode, sSitePw, sResponseData, sCPRequest);
        int iRtn = pClient.fnResponse(sSiteCode, sSitePw, sResponseData);

        // 인증결과 추출
        String sRtnMsg					= "";							// 처리결과 메세지
        String sVNumber					= pClient.getVNumber();			// 가상주민번호 (13byte, 영숫자 조합)
        String sName					= pClient.getName();			// 성명 (EUC-KR)
        String sAgeCode					= pClient.getAgeCode();			// 연령대코드 (0~7: 가이드 참조)
        String sGenderCode				= pClient.getGenderCode();		// 성별 (0:여성, 1: 남성)
        String sBirthDate				= pClient.getBirthDate();		// 생년월일 (YYYYMMDD)
        String sNationalInfo			= pClient.getNationalInfo();	// 내/외국인코드 (0:내국인, 1:외국인)
        String sCPRequestNum			= pClient.getCPRequestNO();		// CP 요청번호
        String sDupInfo					= pClient.getDupInfo();			// 중복가입확인값 (64byte, 개인식별값, DI:Duplicate Info)
        String sCoInfo1				    = pClient.getCoInfo1();			// 연계정보 확인값 (88byte, 개인식별값, CI:Connecting Information)
        String sCIUpdate			    = pClient.getCIUpdate();		// CI 갱신정보 (1~: 가이드 참조)

        // 복호화 처리결과코드에 따른 처리
        if (iRtn == 1)
        {
        /*
            추출된 사용자 정보는 '성명'만 이용자에게 노출 가능합니다.

            사용자 정보를 다른 페이지에서 이용하는 경우,
            암호화 데이터(sResponseData)를 전송 후 해당 페이지에서 복호화하는 형태로 이용해주십시오

            복호화된 사용자 정보를 전달할 경우,
                데이터가 유출되지 않도록 세션처리를 권장드립니다. (예: CP요청번호를 세션에 저장/취득하는 방식)
            form의 hidden 태그를 이용한 전달방식은 데이터 유출 위험이 높으므로 지양해주시기 바랍니다.
        */

            /*
            out.println("가상주민번호 : " + sVNumber + "<br>");
            out.println("이름 : " + sName + "<br>");
            out.println("연령대 코드 : " + sAgeCode + "<br>");
            out.println("성별 코드 : " + sGenderCode + "<br>");
            out.println("생년월일 : " + sBirthDate + "<br>");
            out.println("내/외국인 정보 : " + sNationalInfo + "<br>");
            out.println("CP 요청번호 : " + sCPRequestNum + "<br><br>");
            out.println("중복가입 확인값 (DI) : " + sDupInfo + "<br>");
            out.println("연계정보 확인값 (CI) : " + sCoInfo1 + "<br>");
            out.println("CI 갱신정보 : " + sCIUpdate + "<br>");
            out.println("***** 복호화 된 정보가 정확한지 확인해 주시기 바랍니다. *****<br><br><br><br>");
            */
            sRtnMsg = "정상 처리되었습니다.";

        }
        else if (iRtn == -1 || iRtn == -4)
        {
            sRtnMsg = "복호화 시스템 오류 :<br> 귀사 서버 환경에 맞는 모듈을 이용해주십시오.<br>오류가 지속되는 경우 iRtn 값, 서버 환경정보, 사이트코드를 기재해 문의주시기 바랍니다.";
        }
        else if (iRtn == -6)
        {
            sRtnMsg =	"복호화 처리 오류: 당사에서 이용하는 charset인 EUC-KR이 정상적으로 받아지는 확인해주십시오. <br>오류가 지속되는 경우, 개발 가이드의 <b>\"결과 데이터 확인 방법\"</b>을 참고해주시기 바랍니다.";
        }
        else if (iRtn == -9)
        {
            sRtnMsg = "입력 정보 오류: 복호화 함수에 입력된 파라미터 값을 확인해주십시오.<br>오류가 지속되는 경우, 함수 실행 직전 각 파라미터 값을 로그로 출력해 발송해주시기 바랍니다.";
        }
        else if (iRtn == -12)
        {
            sRtnMsg = "CP 패스워드 불일치: IPIN 서비스 사이트패스워드를 확인해주시기 바랍니다.";
        }
        else if (iRtn == -13)
        {
            sRtnMsg = "CP 요청번호 불일치: 세션에 저장된 CP요청번호(sCPRequest) 값을 확인해주시기 바랍니다.";
        }
        else
        {
            sRtnMsg = "기타오류: iRtn 값 확인 후 NICE평가정보 전산 담당자에게 문의해주시기 바랍니다.";
        }
        model.addAttribute("sVNumber", sVNumber);
        model.addAttribute("sName", sName);
        model.addAttribute("sAgeCode", sAgeCode);
        model.addAttribute("sGenderCode", sGenderCode);
        model.addAttribute("sBirthDate", sBirthDate);
        model.addAttribute("sNationalInfo", sNationalInfo);
        model.addAttribute("sCPRequestNum", sCPRequestNum);
        model.addAttribute("sDupInfo", sDupInfo);
        model.addAttribute("sCoInfo1", sCoInfo1);
        model.addAttribute("sCIUpdate", sCIUpdate);

        model.addAttribute("sRtnMsg", sRtnMsg);
        model.addAttribute("iRtn", iRtn);
        model.addAttribute("sResponseData", sResponseData);
        return "/nice/ipin/result";
    }
    // 문자열 점검 함수
    public String requestReplace (String paramValue, String gubun) {
        String result = "";

        if (paramValue != null) {

            paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

            paramValue = paramValue.replaceAll("\\*", "");
            paramValue = paramValue.replaceAll("\\?", "");
            paramValue = paramValue.replaceAll("\\[", "");
            paramValue = paramValue.replaceAll("\\{", "");
            paramValue = paramValue.replaceAll("\\(", "");
            paramValue = paramValue.replaceAll("\\)", "");
            paramValue = paramValue.replaceAll("\\^", "");
            paramValue = paramValue.replaceAll("\\$", "");
            paramValue = paramValue.replaceAll("'", "");
            paramValue = paramValue.replaceAll("@", "");
            paramValue = paramValue.replaceAll("%", "");
            paramValue = paramValue.replaceAll(";", "");
            paramValue = paramValue.replaceAll(":", "");
            paramValue = paramValue.replaceAll("-", "");
            paramValue = paramValue.replaceAll("#", "");
            paramValue = paramValue.replaceAll("--", "");
            paramValue = paramValue.replaceAll("-", "");
            paramValue = paramValue.replaceAll(",", "");

            if(gubun != "encodeData"){
                paramValue = paramValue.replaceAll("\\+", "");
                paramValue = paramValue.replaceAll("/", "");
                paramValue = paramValue.replaceAll("=", "");
            }
            result = paramValue;

        }
        return result;
    }
}
