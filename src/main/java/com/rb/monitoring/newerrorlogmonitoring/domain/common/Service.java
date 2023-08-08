package com.rb.monitoring.newerrorlogmonitoring.domain.common;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceConfItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ServiceName")
    private String serviceName;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Environment> environments;

    public static Service newService(ServiceConfItem serviceConf) {
        return new Service(null, serviceConf.getServiceName(), new ArrayList<>());
    }

}
