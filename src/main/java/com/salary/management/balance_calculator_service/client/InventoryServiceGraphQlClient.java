package com.salary.management.balance_calculator_service.client;

import com.salary.management.balance_calculator_service.model.BalanceGroupMemberDto;
import com.salary.management.balance_calculator_service.model.ExpenseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
public class InventoryServiceGraphQlClient implements InventoryServiceClient {

    private final HttpGraphQlClient httpGraphQlClient;

    public InventoryServiceGraphQlClient(@Value("${client.inventoryService.baseUrl}") String inventoryServiceBaseUrl) {
        this.httpGraphQlClient = HttpGraphQlClient.builder((WebClient.builder()
                        .baseUrl(inventoryServiceBaseUrl)
                        .build()))
                .build();
    }

    @Override
    public List<ExpenseDto> findAllExpensesFromBalanceGroup(UUID balanceGroupId) {
        var document = """
                query {
                  findAllExpensesFromBalanceGroup(balanceGroupId: "%s") {
                    id,
                    createdAt,
                    updatedAt,
                    name,
                    amount,
                    paidByUserId,
                    needToPayUserId,
                    splitType,
                    resolved
                  }
                }
                """.formatted(balanceGroupId);
        return sendGraphQlRequest(document, ExpenseDto.class, "findAllExpensesFromBalanceGroup");
    }

    @Override
    public List<BalanceGroupMemberDto> findAllGroupMembersFromBalanceGroup(UUID balanceGroupId) {
        var document = """
                query {
                  findAllGroupMembersFromBalanceGroup(balanceGroupId: "%s") {
                       id
                       userId
                       nickname
                     }
                }
                """.formatted(balanceGroupId);
        return sendGraphQlRequest(document, BalanceGroupMemberDto.class, "findAllGroupMembersFromBalanceGroup");
    }

    private <T> List<T> sendGraphQlRequest(String document, Class<T> responseType, String retrieveSection) {
        return httpGraphQlClient.document(document)
                .retrieve(retrieveSection)
                .toEntityList(responseType)
                .block();
    }
}
