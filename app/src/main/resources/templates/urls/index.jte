@import hexlet.code.dto.UrlsPage
@import hexlet.code.utils.NamedRoutes
@import java.time.format.DateTimeFormatter
@import java.time.format.FormatStyle;


@param UrlsPage page
@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">
        <h1>Сайты</h1>
        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th>Имя</th>
                <th class="col-2">Последняя проверка</th>
                <th class="col-1">Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @for(var url : page.getUrls())
                <tr>
                    <td>
                        ${url.getId()}
                    </td>
                    <td>
                        <a href="${NamedRoutes.urlShow(url.getId())}">${url.getName()}</a>
                    </td>
                    <td>
                        @if(!(url.getUrlChecks().isEmpty()))
                            ${DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(url.getLastUrlCheck().getCreatedAt().toLocalDateTime())}
                        @endif
                    </td>
                    <td>
                        @if(!(url.getUrlChecks().isEmpty()))
                            ${url.getLastUrlCheck().getStatusCode()}
                        @endif
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`
)