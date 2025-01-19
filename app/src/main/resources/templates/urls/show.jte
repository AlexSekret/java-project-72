@import hexlet.code.dto.UrlPage
@import java.time.format.DateTimeFormatter
@import java.time.format.FormatStyle
@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">
        <h1>Сайт: ${page.getUrl().getName()}</h1>

        <table class="table table-bordered table-hover mt-3">
            <tbody>
            <tr>
                <td>ID</td>
                <td>${page.getUrl().getId()}</td>
            </tr>
            <tr>
                <td>Имя</td>
                <td>${page.getUrl().getName()}</td>
            </tr>
            <tr>
                <td>Дата создания</td>
                <td>${DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(page.getUrl().getCreatedAt().toLocalDateTime())}</td>
            </tr>
            </tbody>
        </table>

        <h2 class="mt-5">Проверки</h2>
        <form method="post" action="/urls/1/checks">
            <button type="submit" class="btn btn-primary">Запустить проверку</button>
        </form>

        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th class="col-1">Код ответа</th>
                <th>title</th>
                <th>h1</th>
                <th>description</th>
                <th class="col-2">Дата проверки</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>
                    Здесь должен быть ID
                </td>
                <td>
                    Код ответа
                </td>
                <td>
                    заголовок
                </td>
                <td>
                    заголовок первого уровня H1
                </td>
                <td>
                    описание
                </td>
                <td>
                    Дата проверки
                </td>
            </tr>
            </tbody>
        </table>
    </div>
`
)