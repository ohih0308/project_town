package ohih.town.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ohih.town.domain.board.service.BoardServiceImpl;
import org.springframework.web.servlet.HandlerInterceptor;

import static ohih.town.constants.DomainConst.BOARD_ID;

@RequiredArgsConstructor
public class IsBoardActivatedInterceptor implements HandlerInterceptor {

    private final BoardServiceImpl boardService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long boardId = (Long) request.getAttribute(BOARD_ID);
        return boardService.isBoardActivated(boardId);
    }
}
