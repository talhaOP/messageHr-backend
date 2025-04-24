package com.messageHr.messageHr.repo;

import java.util.List;

public interface CustomHrRepository {
    int deleteByIdsOrNamesOrEmails(List<Integer> ids, List<String> names, List<String> emails);
}