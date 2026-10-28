package pl.wsb.rules.lab03;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import pl.wsb.rules.core.JpaModel;
import pl.wsb.rules.core.ProjectApp;
import pl.wsb.rules.core.SinceLab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reguły LAB 03: relacje między encjami i dane powiązane. R3.01–R3.03 czytają pliki .class (ArchUnit),
 * R3.04 uruchamia aplikację na czystej bazie H2 i liczy wiersze z wypełnionym kluczem obcym.
 * Wiele-do-wielu, LAZY i repozytorium są warunkami na ocenę 5: {@link Lab03GradeFiveRulesTest}.
 */
@SinceLab(3)
@DisplayName("LAB 03 · Relacje między tabelami")
class Lab03ProjectRulesTest {

    @Test
    @DisplayName("R3.01 Co najmniej 2 relacje @ManyToOne we własnych encjach, każda z @JoinColumn(name = \"…_id\")")
    void manyToOneWithJoinColumn() {
        Lab03Checks.manyToOneWithJoinColumn(JpaModel.ofProject());
    }

    @Test
    @DisplayName("R3.02 Co najmniej jedna własna encja wskazuje na konto użytkownika (UserAccount)")
    void someEntityReferencesUserAccount() {
        Lab03Checks.someEntityReferencesUserAccount(JpaModel.ofProject());
    }

    @Test
    @DisplayName("R3.03 @OneToMany jest stroną odwrotną relacji: ma mappedBy")
    void oneToManyIsInverseSide() {
        Lab03Checks.oneToManyIsInverseSide(JpaModel.ofProject());
    }

    @Test
    @DisplayName("R3.04 Dane powiązane: aplikacja startuje, a każda relacja @ManyToOne ma w data.sql wiersz z wypełnionym kluczem obcym")
    void everyRelationHasLinkedRows() {
        List<String> unlinked = new ArrayList<>();
        List<String> linked = new ArrayList<>();
        try (ConfigurableApplicationContext context = ProjectApp.startWithHibernateSchema();
             EntityManager em = ProjectApp.entityManagerFactory(context).createEntityManager()) {
            List<EntityType<?>> entities = em.getMetamodel().getEntities().stream()
                    .filter(e -> !JpaModel.USER_ACCOUNT.equals(e.getJavaType().getSimpleName()))
                    .sorted(Comparator.comparing(EntityType::getName))
                    .toList();
            for (EntityType<?> entity : entities) {
                for (SingularAttribute<?, ?> attribute : entity.getDeclaredSingularAttributes()) {
                    if (!isToOne(attribute)) {
                        continue;
                    }
                    long rows = countWithValue(em, entity, attribute);
                    String where = entity.getName() + "." + attribute.getName();
                    (rows > 0 ? linked : unlinked).add(where + " (wierszy z kluczem obcym: " + rows + ")");
                }
            }
        }
        assertThat(unlinked)
                .withFailMessage("Relacje bez danych: %s. W data.sql wiersze tabeli podrzędnej mają w kolumnie klucza obcego numer wiersza "
                                + "tabeli nadrzędnej (np. table_id = 1), a sekcja tabeli nadrzędnej jest wyżej niż sekcja podrzędnej. "
                                + "Relacje z danymi: %s",
                        unlinked, linked)
                .isEmpty();
    }

    private static boolean isToOne(Attribute<?, ?> attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE
                || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE;
    }

    private static long countWithValue(EntityManager em, EntityType<?> entity, SingularAttribute<?, ?> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<?> root = query.from(entity);
        query.select(cb.count(root)).where(cb.isNotNull(root.get(attribute.getName())));
        return em.createQuery(query).getSingleResult();
    }
}
