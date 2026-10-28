package pl.wsb.rules.lab03;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.core.domain.JavaType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import pl.wsb.rules.core.JpaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sprawdzenia reguł LAB 03 (relacje między encjami, pierwsze repozytorium) na modelu {@link JpaModel}.
 * Oddzielone od klas testowych, żeby ten sam kod działał na projekcie zespołu i na przykładach
 * w {@code pl.wsb.rules.examples}. R3.04 (dane powiązane) uruchamia aplikację i jest w {@link Lab03ProjectRulesTest}.
 */
final class Lab03Checks {

    static final int MIN_MANY_TO_ONE = 2;
    static final String REPOSITORY_LAYER = "repository";
    static final String SPRING_DATA_REPOSITORY = "org.springframework.data.repository.Repository";

    /** Nazwa metody zapytania pochodnego Spring Data: prefiks, opcjonalny dodatek (All, First…), „By”, warunek. */
    private static final Pattern DERIVED_QUERY =
            Pattern.compile("^(find|read|get|query|search|stream|count|exists|delete|remove)(\\w*?)By([A-Z]\\w*)$");

    private Lab03Checks() {
    }

    /** R3.01 */
    static void manyToOneWithJoinColumn(JpaModel model) {
        List<JavaField> manyToOne = model.ownFieldsAnnotatedWith(ManyToOne.class);
        List<String> withoutJoinColumn = manyToOne.stream()
                .filter(f -> f.tryGetAnnotationOfType(JoinColumn.class).map(j -> j.name().isBlank()).orElse(true))
                .map(JpaModel::where)
                .toList();
        assertThat(withoutJoinColumn)
                .withFailMessage("Każde pole @ManyToOne ma @JoinColumn(name = \"<tabela_nadrzędna>_id\"), żeby kolumna klucza obcego "
                        + "miała czytelną nazwę w bazie. Dopiszcie @JoinColumn w: %s", withoutJoinColumn)
                .isEmpty();
        assertThat(manyToOne)
                .withFailMessage("Potrzeba co najmniej %d relacji @ManyToOne we własnych encjach, jest %d: %s. "
                                + "Relacja wiele-do-jednego to pole typu encji nadrzędnej z @ManyToOne i @JoinColumn, np. w rezerwacji pole stolika: "
                                + "@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = \"table_id\", nullable = false) private CafeTable table;",
                        MIN_MANY_TO_ONE, manyToOne.size(), manyToOne.stream().map(JpaModel::where).toList())
                .hasSizeGreaterThanOrEqualTo(MIN_MANY_TO_ONE);
    }

    /** R3.02 */
    static void someEntityReferencesUserAccount(JpaModel model) {
        List<String> references = model.ownEntities().stream()
                .flatMap(c -> JpaModel.fields(c).stream())
                .filter(f -> f.isAnnotatedWith(ManyToOne.class) || f.isAnnotatedWith(OneToOne.class))
                .filter(f -> JpaModel.isUserAccount(f.getRawType()))
                .map(JpaModel::where)
                .toList();
        assertThat(references)
                .withFailMessage("Żadna własna encja nie wskazuje na konto użytkownika. Encja osoby korzystającej z aplikacji "
                        + "(klient, gość, autor, właściciel…) ma pole @ManyToOne albo @OneToOne typu UserAccount "
                        + "z @JoinColumn(name = \"user_account_id\"). Konto to logowanie, Wasza encja to dane biznesowe.")
                .isNotEmpty();
    }

    /** R3.03 */
    static void oneToManyIsInverseSide(JpaModel model) {
        List<String> offenders = model.ownFieldsAnnotatedWith(OneToMany.class).stream()
                .filter(f -> f.getAnnotationOfType(OneToMany.class).mappedBy().isBlank())
                .map(JpaModel::where)
                .toList();
        assertThat(offenders)
                .withFailMessage("@OneToMany to tylko strona odwrotna relacji: klucz obcy jest po stronie @ManyToOne. "
                        + "Dopiszcie mappedBy = \"<nazwa pola @ManyToOne w encji podrzędnej>\" w: %s "
                        + "(bez mappedBy Hibernate tworzy dodatkową tabelę łączącą, której nie planowaliście).",
                        offenders)
                .isEmpty();
    }

    /** R3.05 (na ocenę 5): brak zwraca opis, co dopisać. */
    static Optional<String> missingManyToMany(JpaModel model) {
        List<JavaField> manyToMany = model.ownFieldsAnnotatedWith(ManyToMany.class);
        List<String> joinEntities = model.ownEntities().stream()
                .filter(c -> JpaModel.fields(c).stream().filter(f -> f.isAnnotatedWith(ManyToOne.class)).count() >= 2)
                .map(JavaClass::getSimpleName)
                .toList();
        if (manyToMany.isEmpty() && joinEntities.isEmpty()) {
            return Optional.of("Brak relacji wiele-do-wielu. Zamodelujcie ją encją łączącą z dwiema relacjami @ManyToOne "
                    + "i własnymi atrybutami (np. ReservationExtra: rezerwacja + dodatek + ilość). "
                    + "Gdy para nie ma własnych danych, można użyć @ManyToMany z @JoinTable(name = \"…\").");
        }
        List<String> withoutJoinTable = manyToMany.stream()
                .filter(f -> f.getAnnotationOfType(ManyToMany.class).mappedBy().isBlank())
                .filter(f -> f.tryGetAnnotationOfType(JoinTable.class).map(j -> j.name().isBlank()).orElse(true))
                .map(JpaModel::where)
                .toList();
        if (!withoutJoinTable.isEmpty()) {
            return Optional.of("Pole @ManyToMany po stronie właściciela ma @JoinTable(name = \"<tabela_a>_<tabela_b>\"), "
                    + "żeby tabela łącząca miała zaplanowaną nazwę. Dopiszcie @JoinTable w: " + withoutJoinTable);
        }
        return Optional.empty();
    }

    /** R3.06 (na ocenę 5): każda relacja do jednego wiersza pobierana leniwie. */
    static void toOneRelationsAreLazy(JpaModel model) {
        List<String> eager = new ArrayList<>();
        for (JavaField f : model.ownFieldsAnnotatedWith(ManyToOne.class)) {
            if (f.getAnnotationOfType(ManyToOne.class).fetch() != FetchType.LAZY) {
                eager.add(JpaModel.where(f));
            }
        }
        for (JavaField f : model.ownFieldsAnnotatedWith(OneToOne.class)) {
            if (f.getAnnotationOfType(OneToOne.class).fetch() != FetchType.LAZY) {
                eager.add(JpaModel.where(f));
            }
        }
        assertThat(eager)
                .withFailMessage("Relacje @ManyToOne i @OneToOne domyślnie pobierają wiersz nadrzędny od razu (EAGER), także wtedy, "
                        + "gdy nikt o niego nie pyta. Dopiszcie fetch = FetchType.LAZY w: %s", eager)
                .isEmpty();
    }

    /** R3.07 (na ocenę 5): repozytorium Spring Data z zapytaniem pochodnym, które przechodzi przez relację. */
    static void repositoryWithQueryThroughRelation(JpaModel model) {
        List<JavaClass> repositories = model.classes().stream()
                .filter(JavaClass::isInterface)
                .filter(c -> c.isAssignableTo(SPRING_DATA_REPOSITORY))
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .toList();
        assertThat(repositories)
                .withFailMessage("Brak repozytorium. Repozytorium to interfejs w pakiecie %s.<moduł>.repository, np. "
                        + "public interface ReservationRepository extends JpaRepository<Reservation, Long> { … }",
                        model.rootPackage())
                .isNotEmpty();

        List<String> misplaced = repositories.stream()
                .filter(c -> model.layerOf(c).filter(REPOSITORY_LAYER::equals).isEmpty())
                .map(c -> c.getSimpleName() + " (" + c.getPackageName() + ")")
                .toList();
        assertThat(misplaced)
                .withFailMessage("Repozytoria leżą w pakietach %s.<moduł>.repository, obok pakietu entity tego modułu. "
                        + "Przenieście (IntelliJ: Refactor → Move): %s", model.rootPackage(), misplaced)
                .isEmpty();

        List<String> found = new ArrayList<>();
        List<String> hints = new ArrayList<>();
        for (JavaClass repository : repositories) {
            Optional<JavaClass> entity = managedEntity(repository);
            if (entity.isEmpty()) {
                hints.add(repository.getSimpleName() + ": nie widać encji w JpaRepository<Encja, Long>");
                continue;
            }
            List<String> relations = JpaModel.fields(entity.get()).stream()
                    .filter(Lab03Checks::isRelation)
                    .map(JavaField::getName)
                    .toList();
            for (JavaMethod method : repository.getMethods()) {
                Optional<String> relation = relationUsedBy(method.getName(), relations);
                if (relation.isPresent()) {
                    found.add(repository.getSimpleName() + "." + method.getName() + " (przez " + relation.get() + ")");
                }
            }
            if (relations.isEmpty()) {
                hints.add(repository.getSimpleName() + ": encja " + entity.get().getSimpleName() + " nie ma relacji");
            } else {
                hints.add(repository.getSimpleName() + ": relacje encji " + entity.get().getSimpleName() + " to " + relations);
            }
        }
        assertThat(found)
                .withFailMessage("Żadna metoda repozytorium nie pyta przez relację. Dopiszcie zapytanie pochodne, którego nazwa zaczyna się "
                        + "od pola relacji, np. List<Reservation> findByGuest_FullName(String fullName) albo "
                        + "List<Reservation> findByTable_Number(Integer number). Spring Data buduje SQL z nazwy metody. %s", hints)
                .isNotEmpty();
    }

    /** Encja z pierwszego argumentu typowego {@code JpaRepository<Encja, Long>} (albo innego interfejsu Spring Data). */
    static Optional<JavaClass> managedEntity(JavaClass repository) {
        for (JavaType type : repository.getInterfaces()) {
            if (type instanceof JavaParameterizedType parameterized
                    && parameterized.toErasure().isAssignableTo(SPRING_DATA_REPOSITORY)
                    && !parameterized.getActualTypeArguments().isEmpty()) {
                return Optional.of(parameterized.getActualTypeArguments().get(0).toErasure());
            }
        }
        return Optional.empty();
    }

    /** Nazwa pola relacji, od którego zaczyna się warunek w nazwie metody, np. {@code findByGuest_FullName} → {@code guest}. */
    static Optional<String> relationUsedBy(String methodName, List<String> relations) {
        Matcher m = DERIVED_QUERY.matcher(methodName);
        if (!m.matches()) {
            return Optional.empty();
        }
        String condition = m.group(3);
        return relations.stream()
                .filter(r -> condition.startsWith(Character.toUpperCase(r.charAt(0)) + r.substring(1)))
                .findFirst();
    }

    private static boolean isRelation(JavaField f) {
        return f.isAnnotatedWith(ManyToOne.class) || f.isAnnotatedWith(OneToOne.class)
                || f.isAnnotatedWith(OneToMany.class) || f.isAnnotatedWith(ManyToMany.class);
    }
}
