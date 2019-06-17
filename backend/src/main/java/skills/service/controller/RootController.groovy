package skills.service.controller

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import skills.service.controller.exceptions.SkillsValidator
import skills.service.controller.result.model.RequestResult
import skills.service.controller.result.model.UserInfoRes
import skills.service.datastore.services.AccessSettingsStorageService
import skills.service.settings.EmailConnectionInfo
import skills.service.settings.EmailSettingsService
import skills.storage.model.auth.UserRole
import skills.storage.repos.UserRepo

import java.security.Principal

@RestController
@RequestMapping('/root')
@Slf4j
class RootController {

    @Autowired
    AccessSettingsStorageService accessSettingsStorageService

    @Autowired
    EmailSettingsService emailSettingsService

    @Autowired
    UserRepo userRepository

    @GetMapping('/rootUsers')
    @ResponseBody
    List<UserRole> getRootUsers() {
        return accessSettingsStorageService.getRootUsers()
    }

    @GetMapping('/users/{query}')
    @ResponseBody
    List<UserInfoRes> getNonRootUsers(@PathVariable('query') String query) {
        query = query.toLowerCase()
        return accessSettingsStorageService.getNonRootUsers().findAll {
            it.userId.toLowerCase().contains(query)
        }.collect { new UserInfoRes(userRepository.findByUserId(it.userId)) }.unique()
    }

    @GetMapping('/isRoot')
    boolean isRoot(Principal principal) {
        return accessSettingsStorageService.isRoot(principal.name)
    }

    @PutMapping('/addRoot/{userId}')
    RequestResult addRoot(@PathVariable('userId') String userId) {
        accessSettingsStorageService.addRoot(userId)
        return new RequestResult(success: true)
    }

    @DeleteMapping('/deleteRoot/{userId}')
    void deleteRoot(@PathVariable('userId') String userId) {
        SkillsValidator.isTrue(accessSettingsStorageService.getRootAdminCount() > 1, 'At least one root user must exist at all times! Deleting another user will cause no root users to exist!')
        accessSettingsStorageService.deleteRoot(userId)
    }

    @PostMapping('/testEmailSettings')
    boolean testEmailSettings(@RequestBody EmailConnectionInfo emailConnectionInfo) {
        return emailSettingsService.testConnection(emailConnectionInfo)
    }

    @PostMapping('/saveEmailSettings')
    void saveEmailSettings(@RequestBody EmailConnectionInfo emailConnectionInfo) {
        emailSettingsService.updateConnectionInfo(emailConnectionInfo)
    }
}
