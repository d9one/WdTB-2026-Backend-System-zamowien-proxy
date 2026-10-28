package pl.wsb.rules.lab03;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.wsb.rules.core.JpaModel;
import pl.wsb.rules.core.SinceLab;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Autotest reguł LAB 03 (nie dotyczy projektu zespołu): reguły relacji i repozytorium przechodzą dla przykładu kawiarni
 * ({@code pl.wsb.rules.examples.cafe}) i wskazują błędy w celowo złym modelu ({@code pl.wsb.rules.examples.broken}).
 * Uruchamia się razem z regułami LAB 03.
 */
@SinceLab(3)
@DisplayName("Autotest reguł LAB 03 · przykład kawiarni")
class Lab03RulesSelfTest {

    private static final String CAFE = "pl.wsb.rules.examples.cafe";
    private static final String BROKEN = "pl.wsb.rules.examples.broken";
    private static final String NOTHING = "pl.wsb.rules.examples.nothing";

    private static JpaModel model(String rootPackage) {
        return JpaModel.of(new ClassFileImporter().importPackages(rootPackage), rootPackage);
    }

    @Test
    @DisplayName("Przykład kawiarni spełnia R3.01–R3.03 i warunki na ocenę 5 (R3.05–R3.07)")
    void cafeSatisfiesRelationRules() {
        JpaModel cafe = model(CAFE);
        assertThatCode(() -> {
            Lab03Checks.manyToOneWithJoinColumn(cafe);
            Lab03Checks.someEntityReferencesUserAccount(cafe);
            Lab03Checks.oneToManyIsInverseSide(cafe);
            Lab03Checks.toOneRelationsAreLazy(cafe);
            Lab03Checks.repositoryWithQueryThroughRelation(cafe);
        }).doesNotThrowAnyException();
        assertThat(Lab03Checks.missingManyToMany(cafe)).isEmpty();
    }

    @Test
    @DisplayName("Nazwa zapytania pochodnego: warunek zaczyna się od pola relacji")
    void derivedQueryNames() {
        List<String> relations = List.of("guest", "table");
        assertThat(Lab03Checks.relationUsedBy("findByGuest_FullName", relations)).contains("guest");
        assertThat(Lab03Checks.relationUsedBy("findAllByTableNumberOrderByStartsAtDesc", relations)).contains("table");
        assertThat(Lab03Checks.relationUsedBy("countByGuestId", relations)).contains("guest");
        assertThat(Lab03Checks.relationUsedBy("existsByTable_Room", relations)).contains("table");
        assertThat(Lab03Checks.relationUsedBy("findByStatus", relations)).isEmpty();
        assertThat(Lab03Checks.relationUsedBy("findByStartsAtAfter", relations)).isEmpty();
        assertThat(Lab03Checks.relationUsedBy("save", relations)).isEmpty();
    }

    @Test
    @DisplayName("Celowo błędny model jest wskazywany z czytelnym komunikatem")
    void brokenModelIsRejected() {
        JpaModel broken = model(BROKEN);
        assertThatThrownBy(() -> Lab03Checks.manyToOneWithJoinColumn(broken))
                .hasMessageContaining("@JoinColumn").hasMessageContaining("Order.customer");
        assertThatThrownBy(() -> Lab03Checks.manyToOneWithJoinColumn(model(NOTHING)))
                .hasMessageContaining("co najmniej 2 relacji @ManyToOne");
        assertThatThrownBy(() -> Lab03Checks.someEntityReferencesUserAccount(broken))
                .hasMessageContaining("UserAccount");
        assertThatThrownBy(() -> Lab03Checks.oneToManyIsInverseSide(broken))
                .hasMessageContaining("mappedBy").hasMessageContaining("Order.lines");
        assertThat(Lab03Checks.missingManyToMany(broken)).hasValueSatisfying(m -> assertThat(m).contains("wiele-do-wielu"));
        assertThatThrownBy(() -> Lab03Checks.toOneRelationsAreLazy(broken))
                .hasMessageContaining("FetchType.LAZY").hasMessageContaining("Order.customer");
        assertThatThrownBy(() -> Lab03Checks.repositoryWithQueryThroughRelation(broken))
                .hasMessageContaining("<moduł>.repository").hasMessageContaining("OrderRepository");
        assertThatThrownBy(() -> Lab03Checks.repositoryWithQueryThroughRelation(model(NOTHING)))
                .hasMessageContaining("Brak repozytorium");
    }
}
