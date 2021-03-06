package com.codecentric.retailbank.web.controller.api.v1;

import com.codecentric.retailbank.model.domain.Bank;
import com.codecentric.retailbank.model.dto.BankDto;
import com.codecentric.retailbank.repository.helpers.ListPage;
import com.codecentric.retailbank.security.UsersUtil;
import com.codecentric.retailbank.service.BankService;
import com.codecentric.retailbank.web.controller.api.v1.helpers.PageableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.codecentric.retailbank.web.controller.api.v1.helpers.Constant.PAGE_SIZE;

@RestController
@RequestMapping("/api/v1")
public class BankApiController {

    //region FIELDS
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BankService bankService;
    //endregion


    //region HTTP GET
    @GetMapping({"/banks", "/banks/{page}"})
    ResponseEntity<PageableList<BankDto>> banks(@PathVariable("page") Optional<Integer> page,
                                                @RequestParam("details") Optional<String> details) {

        if (details.isPresent()) {
            List<Bank> banks = bankService.getAllBanksByDetails(details.get());
            List<BankDto> bankDtos = banks.stream()
                    .map(bank -> bank == null
                            ? null
                            : new BankDto(bank.getId(), bank.getDetails()))
                    .collect(Collectors.toList());

            PageableList<BankDto> pageableBankDtos = new PageableList<>(0L, bankDtos, 0L, 0L);


            return bankDtos.size() == 0
                    //  404 NOT FOUND
                    ? ResponseEntity.notFound().build()
                    //  200 OK
                    : ResponseEntity.ok().location(URI.create("/banks?details=" + details.get())).body(pageableBankDtos);

        }

        // If pageIndex is less than 1 set it to 1.
        Integer pageIndex = page.isPresent() ? page.get() : 0;
        pageIndex = pageIndex == 0 || pageIndex < 0 || pageIndex == null ?
                0 : pageIndex;

        ListPage<Bank> banks = bankService.getAllBanksByPage(pageIndex, PAGE_SIZE);
        List<BankDto> bankDtos = new ArrayList<>();
        banks.getModels()
                .forEach(
                        x -> bankDtos.add(new BankDto(x.getId(), x.getDetails()))
                );

        PageableList<BankDto> pageableBankDtos = new PageableList<>(pageIndex, bankDtos, banks.getPageCount(), banks.getModelsCount());

        return pageableBankDtos.currentPage == null
                //  404 NOT FOUND
                ? ResponseEntity.notFound().build()
                //  200 OK
                : ResponseEntity.ok().location(URI.create("/banks/" + pageIndex)).body(pageableBankDtos);
    }

    @GetMapping("/bank/{id}")
    ResponseEntity<BankDto> bankById(@PathVariable("id") Long id) {
        Bank bank = bankService.getById(id);
        BankDto bankDto = bank == null
                ? null
                : new BankDto(bank.getId(), bank.getDetails());

        return bank == null
                //  404 NOT FOUND
                ? ResponseEntity.notFound().build()
                //  200 OK
                : ResponseEntity.ok().location(URI.create("/bank/" + id)).body(bankDto);
    }

    @GetMapping("/bank")
    ResponseEntity<BankDto> bankByDetails(@RequestParam("details") String details) {
        Bank bank = bankService.getByDetails(details);
        BankDto bankDto = bank == null
                ? null
                : new BankDto(bank.getId(), bank.getDetails());

        return bank == null
                //  404 NOT FOUND
                ? ResponseEntity.notFound().build()
                //  200 OK
                : ResponseEntity.ok().location(URI.create("/bank?details=" + details)).body(bankDto);
    }
    //endregion

    //region HTTP POST
    @PostMapping("/bank")
    ResponseEntity<BankDto> createBank(@RequestBody BankDto dto) {

        if (!UsersUtil.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Bank result;
        try {
            result = bankService.addBank(dto.getDBModel());
        } catch (Exception e) {
            e.printStackTrace();
            //  400 BAD REQUEST
            return ResponseEntity.badRequest().body(dto);
        }

        //  201 CREATED
        return ResponseEntity.created(URI.create("/bank/" + result.getId())).body(result.getDto());
    }
    //endregion

    //region HTTP PUT
    @PutMapping("/bank")
    ResponseEntity<BankDto> updateBank(@RequestBody BankDto clientDto) {

        if (!UsersUtil.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Bank result;
        try {
            result = bankService.updateBank(clientDto.getDBModel());
        } catch (Exception e) {
            e.printStackTrace();
            //  400 BAD REQUEST
            return ResponseEntity.badRequest().body(clientDto);
        }

        //  200 OK
        return ResponseEntity.ok().location(URI.create("/bank/" + result.getId())).body(result.getDto());
    }
    //endregion

    //region HTTP DELETE
    @DeleteMapping("/bank/{id}")
    ResponseEntity<BankDto> deleteBank(@PathVariable("id") Long id) {

        if (!UsersUtil.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            bankService.deleteBank(id);
        } catch (Exception e) {
            e.printStackTrace();
            //  400 BAD REQUEST
            return ResponseEntity.badRequest().build();
        }

        //  204 NO CONTENT
        return ResponseEntity.noContent().build();
    }
    //endregion
}
