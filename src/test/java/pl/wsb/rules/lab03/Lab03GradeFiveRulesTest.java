package pl.wsb.rules.lab03;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.wsb.rules.core.JpaModel;
import pl.wsb.rules.core.SinceLab;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reguły LAB 03 na ocenę 5. W GitHub Actions działają w osobnym etapie „Ocena 5”, uruchamianym dopiero wtedy,
 * gdy etap „Ocena 4” (reguły obowiązkowe) jest zielony. Lokalnie {@code mvn verify} uruchamia oba zestawy.
 */
@SinceLab(3)
@Tag("ocena5")
@DisplayName("LAB 03 · na ocenę 5")
class Lab03GradeFiveRulesTest {

    @Test
    @DisplayName("R3.05 Relacja wiele-do-wielu: encja łącząca z dwiema @ManyToOne albo @ManyToMany z @JoinTable")
    void manyToManyRelation() {
        met(Lab03Checks.missingManyToMany(JpaModel.ofProject()));
    }

    @Test
    @DisplayName("R3.06 Każda relacja @ManyToOne i @OneToOne ma fetch = FetchType.LAZY")
    void toOneRelationsAreLazy() {
        Lab03Checks.toOneRelationsAreLazy(JpaModel.ofProject());
    }

    @Test
    @DisplayName("R3.07 Repozytorium w pakiecie <moduł>.repository z zapytaniem pochodnym przez relację")
    void repositoryWithQueryThroughRelation() {
        Lab03Checks.repositoryWithQueryThroughRelation(JpaModel.ofProject());
    }

    private static void met(Optional<String> missing) {
        assertThat(missing).withFailMessage(missing.orElse("")).isEmpty();
    }
}
