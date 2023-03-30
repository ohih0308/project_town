package ohih.town.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paging {

    private Long totalCount;
    private Integer totalPages;

    private Integer startPage;
    private Integer endPage;

    public Integer presentPage;

    private Long firstItemIndex;

    private Integer itemsPerPage;
}
